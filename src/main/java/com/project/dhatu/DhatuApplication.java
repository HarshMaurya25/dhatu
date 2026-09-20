package com.project.dhatu;

import org.springframework.ai.model.google.genai.autoconfigure.chat.GoogleGenAiChatAutoConfiguration;
import org.springframework.ai.model.openai.autoconfigure.OpenAiEmbeddingAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication(exclude = {
		GoogleGenAiChatAutoConfiguration.class,
		OpenAiEmbeddingAutoConfiguration.class
})
public class DhatuApplication {

	public static void main(String[] args) {
		SpringApplication.run(DhatuApplication.class, args);
	}

}
