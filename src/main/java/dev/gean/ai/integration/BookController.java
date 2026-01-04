package dev.gean.ai.integration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/books")
public class BookController {

    private final ChatClient chatClient;

    @Value("classpath:/prompts/author.st")
    private Resource authorPrompt;


    @Value("classpath:/prompts/books.st")
    private Resource booksPrompt;

    public BookController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/by-author")
    public Author getBooksByAuthor(@RequestParam(value = "author", defaultValue = "Erick Evans") String author) {
        BeanOutputConverter<Author> outputParser = new BeanOutputConverter(Author.class);
        String format = outputParser.getFormat();

        PromptTemplate promptTemplate = new PromptTemplate(booksPrompt);
        Prompt prompt = promptTemplate.create(Map.of("author", author, "format", format));

        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();
        return outputParser.convert(response.getResult().getOutput().getText());
    }

    @GetMapping("/author/{author}")
    public Map<String, Object> getAuthorsSocialLinks(@PathVariable String author) {
        MapOutputConverter outputParser = new MapOutputConverter();
        String format = outputParser.getFormat();

        PromptTemplate promptTemplate = new PromptTemplate(authorPrompt);
        Prompt prompt = promptTemplate.create(Map.of("author", author, "format", format));

        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();
        return outputParser.convert(response.getResult().getOutput().getText());
    }
}
