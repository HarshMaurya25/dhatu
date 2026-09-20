package com.project.dhatu.service.agents;

import reactor.core.publisher.Flux;

public interface ChatClientService {

    Flux<String> streamResponse(String question, String conversationId);

}
