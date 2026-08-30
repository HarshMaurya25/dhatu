package com.project.dhatu.service.agents;

import reactor.core.publisher.Flux;

import java.util.stream.Stream;

public interface ChatBotService {

    Flux<String> askQuestion(String question,String userName );

}
