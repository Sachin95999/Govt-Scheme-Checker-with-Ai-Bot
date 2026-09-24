package com.assistant.scheme.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.File;

@Configuration
public class VectorStoreConfig {

    @Value("${app.vector-store.path:vector-store.json}")
    private String vectorStorePath;

    @Bean
    @ConditionalOnMissingBean(VectorStore.class)
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore vectorStore = new SimpleVectorStore(embeddingModel);
        File storeFile = new File(vectorStorePath);
        if (storeFile.exists()) {
            try {
                vectorStore.load(storeFile);
            } catch (Exception e) {
                // Log and continue with empty store if file is corrupted
                System.err.println("Could not load vector store file: " + e.getMessage());
            }
        }
        return vectorStore;
    }
}
