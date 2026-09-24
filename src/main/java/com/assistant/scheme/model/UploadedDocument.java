package com.assistant.scheme.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "uploaded_documents")
public class UploadedDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "uploaded_by", nullable = false)
    private String uploadedBy;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }

    // Constructors
    public UploadedDocument() {}

    public UploadedDocument(Long id, String fileName, String filePath, String uploadedBy, LocalDateTime uploadedAt) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = uploadedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    // Static Builder
    public static UploadedDocumentBuilder builder() {
        return new UploadedDocumentBuilder();
    }

    public static class UploadedDocumentBuilder {
        private Long id;
        private String fileName;
        private String filePath;
        private String uploadedBy;
        private LocalDateTime uploadedAt;

        public UploadedDocumentBuilder id(Long id) { this.id = id; return this; }
        public UploadedDocumentBuilder fileName(String fileName) { this.fileName = fileName; return this; }
        public UploadedDocumentBuilder filePath(String filePath) { this.filePath = filePath; return this; }
        public UploadedDocumentBuilder uploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; return this; }
        public UploadedDocumentBuilder uploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; return this; }

        public UploadedDocument build() {
            return new UploadedDocument(id, fileName, filePath, uploadedBy, uploadedAt);
        }
    }
}
