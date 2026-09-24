package com.assistant.scheme.controller;

import com.assistant.scheme.dto.ChatRequest;
import com.assistant.scheme.dto.FeedbackRequest;
import com.assistant.scheme.model.ChatHistory;
import com.assistant.scheme.model.Feedback;
import com.assistant.scheme.repository.FeedbackRepository;
import com.assistant.scheme.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final FeedbackRepository feedbackRepository;

    @Autowired
    public ChatController(ChatService chatService, FeedbackRepository feedbackRepository) {
        this.chatService = chatService;
        this.feedbackRepository = feedbackRepository;
    }

    @PostMapping
    public ResponseEntity<?> sendQuery(@Valid @RequestBody ChatRequest chatRequest, Authentication authentication) {
        String username = authentication.getName();
        ChatHistory chatResponse = chatService.processChat(
                chatRequest.getMessage(),
                username,
                chatRequest.getSessionId(),
                chatRequest.getSchemeName()
        );
        return ResponseEntity.ok(chatResponse);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatHistory>> getChatHistory(Authentication authentication) {
        String username = authentication.getName();
        List<ChatHistory> history = chatService.getUserChatHistory(username);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/feedback")
    public ResponseEntity<?> submitFeedback(@Valid @RequestBody FeedbackRequest feedbackRequest, Authentication authentication) {
        String username = authentication.getName();
        
        // Find user id (or retrieve from database if needed, but in ChatService we retrieve it for history)
        // For simplicity, we save feedback log directly with optional userId.
        Feedback feedback = Feedback.builder()
                .chatId(feedbackRequest.getChatId())
                .rating(feedbackRequest.getRating())
                .comments(feedbackRequest.getComments())
                .build();

        Feedback savedFeedback = feedbackRepository.save(feedback);
        return ResponseEntity.ok(Map.of("message", "Feedback submitted successfully", "id", savedFeedback.getId()));
    }
}
