package dev.gean.ai.integration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class SongsController {

    private final ChatClient chatClient;

    @Value("classpath:/prompts/song-list.st")
    private Resource songsPrompt;

    public SongsController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/songs")
    public List<String> getSongsByArtist(@RequestParam(value = "artist", defaultValue = "Taylor Swift") String artist) {
        ListOutputConverter outputParser = new ListOutputConverter();
        PromptTemplate promptTemplate = new PromptTemplate(songsPrompt);
        Prompt prompt = promptTemplate.create(Map.of("artist", artist, "format", outputParser.getFormat()));
        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();
        return outputParser.convert(response.getResult().getOutput().getText());
    }

    @GetMapping("/songs-no-parser")
    public String noOutputParser(@RequestParam(value = "artist", defaultValue = "Taylor Swift") String artist) {
        PromptTemplate promptTemplate = new PromptTemplate(songsPrompt);
        Prompt prompt = promptTemplate.create(Map.of("artist", artist));
        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();
        return response.getResult().getOutput().getText();
    }
}
