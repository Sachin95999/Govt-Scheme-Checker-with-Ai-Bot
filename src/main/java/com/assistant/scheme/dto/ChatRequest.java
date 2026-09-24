package com.assistant.scheme.dto;

import jakarta.validation.constraints.NotBlank;

public class ChatRequest {
    @NotBlank(message = "Message cannot be empty")
    private String message;

    @NotBlank(message = "Session ID is required")
    private String sessionId;

    private String schemeName;

    // Default Constructor
    public ChatRequest() {}

    // Getters and Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getSchemeName() { return schemeName; }
    public void setSchemeName(String schemeName) { this.schemeName = schemeName; }
}
