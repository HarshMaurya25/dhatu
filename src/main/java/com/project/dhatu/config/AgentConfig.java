package com.project.dhatu.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class AgentConfig {

    @Value("classpath:query/TranslationQuery.st")
    private Resource translationQuery;


    @Bean
    public TranslationQueryTransformer translationQueryTransformer(ChatClient.Builder chatClientBuilder){
        PromptTemplate promptTemplate =
                PromptTemplate
                        .builder()
                        .resource(translationQuery)
                        .build();

        return TranslationQueryTransformer
                .builder()
                .targetLanguage("English")
                .promptTemplate(promptTemplate)
                .chatClientBuilder(chatClientBuilder.clone())
                .build();
    }

}
