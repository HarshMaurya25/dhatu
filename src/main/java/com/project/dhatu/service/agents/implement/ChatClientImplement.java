package com.project.dhatu.service.agents.implement;

import com.project.dhatu.service.agents.ChatClientService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatClientImplement implements ChatClientService {

    private final ChatClient chatClient;
    private final ChatClient languageChatClient;

    public ChatClientImplement(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
        this.languageChatClient = chatClient.build();
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
