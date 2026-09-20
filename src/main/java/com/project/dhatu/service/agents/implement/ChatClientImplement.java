package com.project.dhatu.service.agents.implement;

import com.project.dhatu.config.VectorStoreConfig;
import com.project.dhatu.service.agents.ChatClientService;
import com.project.dhatu.service.tools.DhatuTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.stream.Collectors;

@Service
public class ChatClientImplement implements ChatClientService {

    private final ChatClient chatClient;

    public ChatClientImplement(
            ChatClient.Builder builder,
            ChatMemoryRepository chatMemoryRepository,
            VectorStore vectorStore,
            VectorStoreConfig vectorStoreConfig,
            DhatuTools dhatuTools,
            @Value("classpath:query/SystemPrompt.st") Resource systemPrompt,
            @Value("classpath:query/RewriteQuery.st") Resource rewritePrompt,
            @Value("${chat.maxMessage}") int maxMessage
    ) {
        ChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(maxMessage)
                .build();

        MessageChatMemoryAdvisor chatMemoryAdvisor = MessageChatMemoryAdvisor
                .builder(chatMemory)
                .order(1)
                .build();

        RetrievalAugmentationAdvisor.Builder retrieverAdvisor =
                RetrievalAugmentationAdvisor
                        .builder()
                        .queryTransformers(
                                query -> {
                                    String historyText = query.history()
                                            .stream()
                                            .filter(m -> m.getMessageType() == MessageType.USER)
                                            .map(m -> m.getMessageType() + ": " + m.getText())
                                            .collect(Collectors.joining("\n"));

                                    String enrichedText = historyText.isEmpty()
                                            ? query.text()
                                            : "Conversation History:"
                                            + historyText
                                            + "\nCurrent Query: " + query.text();

                                    return Query.builder()
                                            .text(enrichedText)
                                            .history(query.history())
                                            .context(query.context())
                                            .build();
                                },
                                RewriteQueryTransformer
                                        .builder()
                                        .chatClientBuilder(builder.clone())
                                        .targetSearchSystem("an Ancient Indian Metallurgy RAG knowledge base")
                                        .promptTemplate(
                                                PromptTemplate
                                                        .builder()
                                                        .resource(rewritePrompt)
                                                        .build()
                                        )
                                        .build()
                        )
                        .documentRetriever(
                                VectorStoreDocumentRetriever
                                        .builder()
                                        .vectorStore(vectorStore)
                                        .similarityThreshold(vectorStoreConfig.similarityThreshold())
                                        .topK(vectorStoreConfig.topK())
                                        .build()
                        )
                        .order(2);

        this.chatClient = builder
                .defaultAdvisors(
                        chatMemoryAdvisor,
                        retrieverAdvisor.build(),
                        new SimpleLoggerAdvisor()
                )
                .defaultSystem(systemPrompt)
                .defaultTools(dhatuTools)
                .build();
    }

    @Override
    public Flux<String> streamResponse(String question, String conversationId) {
        return chatClient
                .prompt()
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(question)
                .stream()
                .content()
                .timeout(Duration.ofSeconds(30))
                .onErrorReturn("[The AI service is currently unavailable. Please try again.]");
    }
}
