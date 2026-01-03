package dev.gean.ai.integration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/dad-joke")
    public String joke(@RequestParam(value = "message", defaultValue = "tell me a dad joke") String message){
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
