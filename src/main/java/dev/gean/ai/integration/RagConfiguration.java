package dev.gean.ai.integration;


import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

@Configuration
public class RagConfiguration {

    private static final Logger log = Logger.getLogger(RagConfiguration.class.getName());

    @Value("classpath:/docs/olympic-faq.txt")
    private Resource olympicFaqResource;

    @Value("vectorstore.json")
    private String vectorStoreName;

    @Bean
    EmbeddingModel embeddingModel(OllamaEmbeddingModel ollamaEmbeddingModel) {
        return ollamaEmbeddingModel;
    }


    @Bean
    SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel).build();
        File vectorStoreFile = getVectorStoreFile();
        if (vectorStoreFile.exists()) {
            log.info("Vector Store file exists!");
            simpleVectorStore.load(vectorStoreFile);
        } else {
            log.info("Vector Store file does not exists, loading documents...");
            TextReader textReader = new TextReader(olympicFaqResource);
            textReader.getCustomMetadata().put("filename", "olympic-faq.txt");
            List<Document> documents = textReader.get();
            TokenTextSplitter textSplitter = new TokenTextSplitter();
            List<Document> splitDocuments = textSplitter.apply(documents);

            simpleVectorStore.add(splitDocuments);
            simpleVectorStore.save(vectorStoreFile);
        }

        return simpleVectorStore;
    }

    private File getVectorStoreFile() {
        Path path = Paths.get("src", "main", "resources", "data");
        String absolutePath = path.toFile().getAbsoluteFile() + "/" + vectorStoreName;
        return new File(absolutePath);
    }
}
