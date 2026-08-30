package com.project.dhatu.service.agents.implement;

import com.project.dhatu.config.VectorStoreConfig;
import com.project.dhatu.service.agents.ChatClientService;
import org.hibernate.sql.ast.tree.expression.QueryTransformer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ChatClientImplement implements ChatClientService {

    private final ChatClient chatClient;
    public ChatClientImplement(
            ChatClient.Builder builder,
            ChatMemoryRepository chatMemoryRepository,
            VectorStore vectorStore,
            VectorStoreConfig vectorStoreConfig,
            @Value("classpath:query/SystemPrompt.st") Resource systemPrompt,
            @Value("classpath:query/RewriteQuery.st") Resource rewritePrompt
    ) {
        ChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(5)
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
                                    String historyText = query.history().stream()
                                            .map(m -> m.getMessageType() + ": " + m.getText())
                                            .collect(Collectors.joining("\n"));

                                    String enrichedText = historyText.isEmpty()
                                            ? query.text()
                                            : "Conversation History:\n"
                                            + historyText
                                            + "\n\nCurrent Query: " + query.text();

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
                                        .similarityThreshold(vectorStoreConfig.getSimilarityThreshold())
                                        .topK(vectorStoreConfig.getTopK())
                                        .build()
                        )
                        .order(2);

        this.chatClient = builder
                .defaultAdvisors(
                        chatMemoryAdvisor,
                        retrieverAdvisor.build(),
                        new SimpleLoggerAdvisor()
                )
                .build();
    }

    @Override
    public ChatClient getChatClient() {
        return chatClient;
    }
}
