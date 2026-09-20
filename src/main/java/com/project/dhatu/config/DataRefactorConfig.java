package com.project.dhatu.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataRefactorConfig {

    @Bean
    public DocumentTransformer whitespaceCleanerTransformer() {
        return documents -> documents.stream()
                .map(doc -> new Document(
                        doc.getText()
                                .replaceAll("[ \\t]{2,}", " ")
                                .replaceAll("\\n{3,}", "\n\n")
                                .trim(),
                        doc.getMetadata()
                ))
                .toList();
    }
}
