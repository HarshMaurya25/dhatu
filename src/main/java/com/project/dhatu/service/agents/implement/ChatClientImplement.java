package com.project.dhatu.service.agents.implement;

import com.project.dhatu.service.agents.ChatClientService;
import org.hibernate.sql.ast.tree.expression.QueryTransformer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class ChatClientImplement implements ChatClientService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ChatClient languageChatClient;
    private final TranslationQueryTransformer translationQueryTransformer;


    public ChatClientImplement(ChatClient.Builder builder, ChatMemoryRepository chatMemoryRepository, VectorStore vectorStore, TranslationQueryTransformer translationQueryTransformer) {
        this.vectorStore = vectorStore;
        ChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .chatMemoryRepository(chatMemoryRepository)
                .build();

//        Bean in agent Config
        this.translationQueryTransformer = translationQueryTransformer;

        RetrievalAugmentationAdvisor.Builder retrieverAdvisor =
                RetrievalAugmentationAdvisor
                        .builder()
                        .documentRetriever(
                                VectorStoreDocumentRetriever
                                        .builder()
                                        .vectorStore(this.vectorStore)
                                        .similarityThreshold(0.72)
                                        .topK(4)
                                        .build()
                        );

        this.chatClient = builder
                .defaultAdvisors(retrieverAdvisor.build())
                .build();

        RetrievalAugmentationAdvisor langRetrieverAdvisor = retrieverAdvisor
                .queryTransformers(translationQueryTransformer)
                .build();

        this.languageChatClient = builder
                .defaultAdvisors(langRetrieverAdvisor)
                .build();
    }

    @Override
    public ChatClient getChatClient() {
        return chatClient;
    }

    @Override
    public ChatClient getLanguageChatClient() {
        return languageChatClient;
    }
}
