package com.project.dhatu.service.agents.implement;

import com.project.dhatu.service.agents.ChatBotService;
import com.project.dhatu.service.agents.ChatClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class ChatBotImplement implements ChatBotService {

    private static final Logger log = LoggerFactory.getLogger(ChatBotImplement.class);
    private final ChatClientService chatClientService;

    private static final List<String> IDENTITY_TRIGGERS = List.of(
            "who are you", "what are you", "your name", "are you gpt",
            "are you claude", "are you gemini", "which model", "what model",
            "what llm", "language model", "who made you", "who created you",
            "your creator", "your developer", "openai", "anthropic",
            "tell me about yourself", "introduce yourself"
    );

    private static final String IDENTITY_RESPONSE =
            "I am Dhatu, an AI research assistant specialising in Ancient Indian Metallurgy. " +
            "I can only answer questions within this domain.";

    public ChatBotImplement(ChatClientService chatClientService) {
        this.chatClientService = chatClientService;
    }

    @Override
    public Flux<String> askQuestion(String question, String userName) {
        String lower = question.toLowerCase();
        boolean isIdentityQuestion = IDENTITY_TRIGGERS.stream().anyMatch(lower::contains);

        if (isIdentityQuestion) {
            log.debug("Identity question intercepted, returning fixed response");
            return Flux.just(IDENTITY_RESPONSE);
        }

        return chatClientService.streamResponse(question, userName);
    }
}
