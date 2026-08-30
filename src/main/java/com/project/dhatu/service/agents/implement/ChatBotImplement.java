package com.project.dhatu.service.agents.implement;

import com.project.dhatu.service.agents.ChatBotService;
import com.project.dhatu.service.agents.ChatClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatBotImplement implements ChatBotService {

    private static final Logger log = LoggerFactory.getLogger(ChatBotImplement.class);
    private final ChatClientService chatClientService;

    public ChatBotImplement(ChatClientService chatClientService, TranslationQueryTransformer translationQueryTransformer) {
        this.chatClientService = chatClientService;
    }

    public Flux<String> askQuestion(String question,String userName, String lang) {
        return chatClientService.getChatClient()
                .prompt()
                .advisors(
                        advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, userName)
                )
                .user(question)
                .stream()
                .content();
    }
}
