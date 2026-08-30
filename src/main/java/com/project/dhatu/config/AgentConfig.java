package com.project.dhatu.config;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfig {
    @Bean
    public DocumentTransformer textCleaner() {
        return docs -> docs.stream().map(doc -> {
            String cleaned = doc.getText()
                    .replaceAll("[ \\t]{2,}", " ")
                    .replaceAll("(?m)^[ \\t]+", "")
                    .replaceAll("\\n{3,}", "\n\n")
                    .trim();
            return new Document(cleaned, doc.getMetadata());
        }).toList();
    }
}
