package com.project.dhatu.service.agents;

import reactor.core.publisher.Flux;

public interface ChatBotService {

    Flux<String> askQuestion(String question,String userName );

}
