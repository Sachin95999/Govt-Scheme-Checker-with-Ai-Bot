package com.assistant.scheme.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_history")
public class ChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "user_message", columnDefinition = "TEXT", nullable = false)
    private String userMessage;

    @Column(name = "ai_response", columnDefinition = "TEXT", nullable = false)
    private String aiResponse;

    @Column(name = "sources_json", columnDefinition = "TEXT")
    private String sourcesJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructors
    public ChatHistory() {}

    public ChatHistory(Long id, Long userId, String sessionId, String userMessage, String aiResponse,
                       String sourcesJson, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.sessionId = sessionId;
        this.userMessage = userMessage;
        this.aiResponse = aiResponse;
        this.sourcesJson = sourcesJson;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getUserMessage() { return userMessage; }
    public void setUserMessage(String userMessage) { this.userMessage = userMessage; }

    public String getAiResponse() { return aiResponse; }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }

    public String getSourcesJson() { return sourcesJson; }
    public void setSourcesJson(String sourcesJson) { this.sourcesJson = sourcesJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Static Builder
    public static ChatHistoryBuilder builder() {
        return new ChatHistoryBuilder();
    }

    public static class ChatHistoryBuilder {
        private Long id;
        private Long userId;
        private String sessionId;
        private String userMessage;
        private String aiResponse;
        private String sourcesJson;
        private LocalDateTime createdAt;

        public ChatHistoryBuilder id(Long id) { this.id = id; return this; }
        public ChatHistoryBuilder userId(Long userId) { this.userId = userId; return this; }
        public ChatHistoryBuilder sessionId(String sessionId) { this.sessionId = sessionId; return this; }
        public ChatHistoryBuilder userMessage(String userMessage) { this.userMessage = userMessage; return this; }
        public ChatHistoryBuilder aiResponse(String aiResponse) { this.aiResponse = aiResponse; return this; }
        public ChatHistoryBuilder sourcesJson(String sourcesJson) { this.sourcesJson = sourcesJson; return this; }
        public ChatHistoryBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ChatHistory build() {
            return new ChatHistory(id, userId, sessionId, userMessage, aiResponse, sourcesJson, createdAt);
        }
    }
}
