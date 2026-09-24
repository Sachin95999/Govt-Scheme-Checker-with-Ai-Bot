package com.assistant.scheme.service;

import com.assistant.scheme.model.ChatHistory;
import com.assistant.scheme.repository.ChatHistoryRepository;
import com.assistant.scheme.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatModel chatModel;
    private final VectorStore vectorStore;
    private final DocumentService documentService;
    private final ChatHistoryRepository chatHistoryRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @org.springframework.beans.factory.annotation.Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Autowired
    public ChatService(ChatModel chatModel, VectorStore vectorStore,
                       DocumentService documentService,
                       ChatHistoryRepository chatHistoryRepository,
                       UserRepository userRepository, ObjectMapper objectMapper) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
        this.documentService = documentService;
        this.chatHistoryRepository = chatHistoryRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public ChatHistory processChat(String query, String username, String sessionId) {
        return processChat(query, username, sessionId, null);
    }

    public ChatHistory processChat(String query, String username, String sessionId, String schemeName) {
        // 1. Perform fallback/vector search with scheme filtering
        List<Document> similarDocuments = documentService.searchFallback(query, 4, schemeName);

        if ((similarDocuments == null || similarDocuments.isEmpty()) && vectorStore != null) {
            try {
                similarDocuments = vectorStore.similaritySearch(
                        SearchRequest.query(query).withTopK(4)
                );
            } catch (Exception e) {
                System.err.println("Vector search failed: " + e.getMessage());
            }
        }

        // 2. Format Context and collect sources
        String context = "No document context found. Answer based on general knowledge but advise user to verify with official sources.";
        List<Map<String, Object>> sourcesList = new ArrayList<>();

        if (similarDocuments != null && !similarDocuments.isEmpty()) {
            context = similarDocuments.stream()
                    .map(doc -> {
                        String src = (String) doc.getMetadata().getOrDefault("source", "Unknown Document");
                        Object pageObj = doc.getMetadata().getOrDefault("page", 1);
                        return String.format("Source Document: %s, Page: %s\nContent:\n%s\n---", src, pageObj.toString(), doc.getContent());
                    })
                    .collect(Collectors.joining("\n"));

            for (Document doc : similarDocuments) {
                Map<String, Object> sourceMap = new HashMap<>();
                sourceMap.put("source", doc.getMetadata().getOrDefault("source", "Unknown Document"));
                sourceMap.put("page", doc.getMetadata().getOrDefault("page", 1));
                if (!sourcesList.contains(sourceMap)) {
                    sourcesList.add(sourceMap);
                }
            }
        }

        // 3. Construct System Prompt & Call ChatModel or generate RAG response
        String targetInfo = (schemeName != null && !schemeName.trim().isEmpty()) ? " Target Scheme Focus: " + schemeName + "." : "";
        String systemInstruction = "You are a professional and helpful Government Schemes Assistant." + targetInfo + "\n" +
                "You must assist the user using the official document contexts provided below.\n" +
                "IMPORTANT: Focus exclusively on the scheme requested by the user. Do not include information about unrelated schemes.\n" +
                "For every claim or detail you state, cite the source filename and page number in square brackets (e.g., [pmkisan.pdf, Page 1]).\n" +
                "Keep your answers clear, concise, and beautifully structured.";

        String userPrompt = String.format("Context Documents:\n%s\n\nQuestion: %s", context, query);
        String fullPrompt = systemInstruction + "\n\n" + userPrompt;

        String aiResponse = null;
        if (!"dummy-key-to-allow-startup".equals(apiKey)) {
            try {
                aiResponse = chatModel.call(fullPrompt);
            } catch (Exception e) {
                System.err.println("LLM chatModel.call failed: " + e.getMessage());
            }
        }

        if (aiResponse == null || aiResponse.trim().isEmpty() || "dummy-key-to-allow-startup".equals(apiKey)) {
            aiResponse = generateSynthesizedResponse(query, similarDocuments, schemeName);
        }

        // 4. Save to Chat History
        String sourcesJson = "[]";
        try {
            sourcesJson = objectMapper.writeValueAsString(sourcesList);
        } catch (Exception e) {
            System.err.println("Failed to serialize sources: " + e.getMessage());
        }

        Long userId = userRepository.findByUsername(username)
                .map(u -> u.getId())
                .orElse(null);

        ChatHistory chatHistory = ChatHistory.builder()
                .userId(userId)
                .sessionId(sessionId)
                .userMessage(query)
                .aiResponse(aiResponse)
                .sourcesJson(sourcesJson)
                .build();

        return chatHistoryRepository.save(chatHistory);
    }

    private String generateSynthesizedResponse(String query, List<Document> documents, String schemeName) {
        if (documents == null || documents.isEmpty()) {
            if (schemeName != null && !schemeName.trim().isEmpty()) {
                return "I searched official welfare records for **" + schemeName + "**, but could not find matching documents for your exact question. Please verify your query or select a different scheme.";
            }
            return "I searched official welfare records, but could not find matching documents for your query. Please rephrase or select a specific scheme like *PM Kisan*, *Pradhan Mantri Awas Yojana*, *Sukanya Samriddhi*, or *Rajasthan Swasthya Bima*.";
        }

        StringBuilder sb = new StringBuilder();
        String primarySchemeTitle = (schemeName != null && !schemeName.trim().isEmpty()) ? schemeName : "Official Scheme Information";
        
        sb.append("### 🏛️ ").append(primarySchemeTitle).append("\n\n");
        sb.append("Here is the official document context retrieved regarding your inquiry:\n\n");

        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            String src = (String) doc.getMetadata().getOrDefault("source", "Document.pdf");
            Object page = doc.getMetadata().getOrDefault("page", 1);
            String content = doc.getContent().trim();

            sb.append("**Official Document Citation**: [").append(src).append(", Page ").append(page).append("]\n\n");
            sb.append(content).append("\n\n");
            if (i < documents.size() - 1) {
                sb.append("---\n\n");
            }
        }

        sb.append("💡 *Verified from official government notification files.*");

        return sb.toString();
    }

    public List<ChatHistory> getUserChatHistory(String username) {
        return userRepository.findByUsername(username)
                .map(user -> chatHistoryRepository.findByUserIdOrderByCreatedAtDesc(user.getId()))
                .orElse(new ArrayList<>());
    }
}
