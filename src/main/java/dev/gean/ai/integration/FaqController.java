package dev.gean.ai.integration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FaqController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("classpath:/prompts/rag-prompt-template.st")
    private Resource ragPromptTemplate;

    public FaqController(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
    }

    @GetMapping("/faq")
    public String faq(@RequestParam(value = "message", defaultValue = "how can i buy tickets for the Olympic Games Paris 2024 ?") String message) {

        SearchRequest.Builder builder = SearchRequest.builder();
        SearchRequest similaritySearch = builder.query(message).topK(2).build();
        List<Document> similarDocuments = vectorStore.similaritySearch(similaritySearch);
        List<String> contentList = similarDocuments.stream().map(Document::getText).toList();

        PromptTemplate promptTemplate = new PromptTemplate(ragPromptTemplate);
        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("input", message);
        promptParameters.put("documents", String.join("\n", contentList));
        Prompt prompt = promptTemplate.create(promptParameters);

        return chatClient.prompt(prompt).call().content();
    }

}
