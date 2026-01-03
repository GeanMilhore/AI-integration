package dev.gean.ai.integration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StarWarsController {

    private final ChatClient chatClient;

    public StarWarsController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/star-wars-jokes")
    public String starWarsJokes() {
        var system = new SystemMessage("Your primary function is to tell star wars jokes. If someone asks you for any other type of jokes just tell them you only know star wars jokes.");
        var user = new UserMessage("Tell me a joke that has nothing to do with star wars");
        Prompt prompt = new Prompt(system, user);
        return chatClient.prompt(prompt).call().content();
    }
}
