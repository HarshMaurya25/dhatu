package com.project.dhatu.service.agents;

import org.springframework.ai.chat.client.ChatClient;

public interface ChatClientService {

    ChatClient getChatClient();
    ChatClient getLanguageChatClient();

}
