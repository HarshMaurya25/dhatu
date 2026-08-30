package com.project.dhatu.service.agents;

import java.util.stream.Stream;

public interface ChatBotService {

    Stream<String> askQuestion(String question,String lang);

}
