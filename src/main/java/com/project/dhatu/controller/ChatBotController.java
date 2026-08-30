package com.project.dhatu.controller;

import com.project.dhatu.service.agents.ChatBotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai/bot")
public class ChatBotController {

    private final ChatBotService chatBotService;

    public ChatBotController(ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
    }

    @GetMapping("/ask")
    public ResponseEntity<Flux<String>> askQuestion(
            @RequestParam("question") String question,
            @RequestParam("name") String username,
            @RequestParam("lang") String lang
    ){
        return ResponseEntity.ok(chatBotService.askQuestion(question, username, lang));
    }

}
