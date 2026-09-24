package com.assistant.scheme.controller;

import com.assistant.scheme.model.Scheme;
import com.assistant.scheme.model.UploadedDocument;
import com.assistant.scheme.model.Feedback;
import com.assistant.scheme.repository.FeedbackRepository;
import com.assistant.scheme.repository.UserRepository;
import com.assistant.scheme.repository.ChatHistoryRepository;
import com.assistant.scheme.repository.SchemeRepository;
import com.assistant.scheme.service.DocumentService;
import com.assistant.scheme.service.SchemeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AdminController {

    private final DocumentService documentService;
    private final SchemeService schemeService;
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final ChatHistoryRepository chatHistoryRepository;
    private final SchemeRepository schemeRepository;

    @Autowired
    public AdminController(DocumentService documentService, SchemeService schemeService,
                           FeedbackRepository feedbackRepository, UserRepository userRepository,
                           ChatHistoryRepository chatHistoryRepository, SchemeRepository schemeRepository) {
        this.documentService = documentService;
        this.schemeService = schemeService;
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.chatHistoryRepository = chatHistoryRepository;
        this.schemeRepository = schemeRepository;
    }

    // Mapping upload here as well to be safe
    @PostMapping("/documents/upload")
    public ResponseEntity<?> uploadDocument(@RequestParam("file") MultipartFile file, Authentication authentication) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "File is empty"));
        }
        if (!file.getContentType().equalsIgnoreCase("application/pdf")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Only PDF files are supported"));
        }

        try {
            String uploadedBy = authentication.getName();
            UploadedDocument doc = documentService.uploadAndIngest(file, uploadedBy);
            return ResponseEntity.ok(doc);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to upload document: " + e.getMessage()));
        }
    }

    @GetMapping("/admin/documents")
    public ResponseEntity<List<UploadedDocument>> listDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @GetMapping("/admin/feedback")
    public ResponseEntity<List<Feedback>> listFeedback() {
        return ResponseEntity.ok(feedbackRepository.findAll());
    }

    @PostMapping("/admin/schemes")
    public ResponseEntity<?> addScheme(@Valid @RequestBody Scheme scheme) {
        Scheme saved = schemeService.saveScheme(scheme);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/admin/schemes/{id}")
    public ResponseEntity<?> deleteScheme(@PathVariable Long id) {
        schemeService.deleteScheme(id);
        return ResponseEntity.ok(Map.of("message", "Scheme deleted successfully"));
    }

    @GetMapping("/admin/analytics")
    public ResponseEntity<?> getAnalytics() {
        long userCount = userRepository.count();
        long schemeCount = schemeRepository.count();
        long documentCount = documentService.getAllDocuments().size();
        long chatCount = chatHistoryRepository.count();
        long feedbackCount = feedbackRepository.count();

        return ResponseEntity.ok(Map.of(
                "users", userCount,
                "schemes", schemeCount,
                "documents", documentCount,
                "chats", chatCount,
                "feedback", feedbackCount
        ));
    }
}
