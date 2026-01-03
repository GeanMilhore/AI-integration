package dev.gean.ai.integration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimplePromptController {

    private final ChatClient chatClient;

    public SimplePromptController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("")
    public String simplePrompt() {
        return chatClient
                .prompt(new Prompt("tell me an dad joke"))
                .call()
                .content();
    }
}
