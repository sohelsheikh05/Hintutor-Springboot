package com.Hintutor.Hinttutor.Controller;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final ChatClient chatClient;

    public TestController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/test-ai")
    public String testAi(@RequestParam String question) {

        return chatClient
                .prompt()
                .user(question)
                .call()
                .content();
    }
}