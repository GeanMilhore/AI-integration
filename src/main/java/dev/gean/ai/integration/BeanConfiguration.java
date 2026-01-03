package dev.gean.ai.integration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClient) {
        return chatClient.build();
    }
}
