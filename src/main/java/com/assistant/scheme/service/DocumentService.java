package com.assistant.scheme.service;

import com.assistant.scheme.model.UploadedDocument;
import com.assistant.scheme.repository.UploadedDocumentRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DocumentService {

    private final VectorStore vectorStore;
    private final UploadedDocumentRepository documentRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.vector-store.path:vector-store.json}")
    private String vectorStorePath;

    private final List<Document> documentRegistry = new java.util.concurrent.CopyOnWriteArrayList<>();

    private static final java.util.Set<String> STOP_WORDS = new java.util.HashSet<>(java.util.Arrays.asList(
            "what", "is", "are", "the", "a", "an", "for", "in", "of", "and", "or", "to", "how", "can", "i",
            "my", "get", "does", "which", "do", "you", "about", "with", "from", "on", "by", "at", "it", "this", "that"
    ));

    @Autowired
    public DocumentService(VectorStore vectorStore, UploadedDocumentRepository documentRepository) {
        this.vectorStore = vectorStore;
        this.documentRepository = documentRepository;
    }

    public UploadedDocument uploadAndIngest(MultipartFile file, String uploadedBy) throws IOException {
        // Ensure upload directory exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file to disk
        String fileName = String.valueOf(System.currentTimeMillis()) + "_" + file.getOriginalFilename();
        Path targetFilePath = uploadPath.resolve(fileName);
        file.transferTo(targetFilePath.toFile());

        // Save metadata to MySQL
        UploadedDocument uploadedDocument = UploadedDocument.builder()
                .fileName(file.getOriginalFilename())
                .filePath(targetFilePath.toString())
                .uploadedBy(uploadedBy)
                .build();
        UploadedDocument savedDoc = documentRepository.save(uploadedDocument);

        // Process PDF and Ingest to Vector Database & Registry
        ingestPdf(targetFilePath.toFile(), file.getOriginalFilename());

        return savedDoc;
    }

    public List<UploadedDocument> getAllDocuments() {
        return documentRepository.findAll();
    }

    public void reloadAllLocalPdfs() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                return;
            }
            File[] pdfFiles = uploadPath.toFile().listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
            if (pdfFiles == null) return;

            for (File pdf : pdfFiles) {
                ingestPdf(pdf, pdf.getName());
            }
        } catch (Exception e) {
            System.err.println("Error reloading local PDFs: " + e.getMessage());
        }
    }

    public List<Document> searchFallback(String query, int limit) {
        return searchFallback(query, limit, null);
    }

    public List<Document> searchFallback(String query, int limit, String schemeName) {
        if (documentRegistry.isEmpty()) {
            reloadAllLocalPdfs();
        }
        if (documentRegistry.isEmpty()) {
            return new ArrayList<>();
        }

        // Identify target scheme key from explicit schemeName or query text
        String targetPdfKey = resolvePdfKeyword(schemeName);
        if (targetPdfKey == null && query != null) {
            targetPdfKey = resolvePdfKeyword(query);
        }

        final String activePdfKey = targetPdfKey;

        // Filter registry by target PDF if a scheme is specified/detected
        List<Document> candidateDocs = documentRegistry;
        if (activePdfKey != null) {
            List<Document> filtered = documentRegistry.stream()
                    .filter(doc -> {
                        String source = doc.getMetadata().getOrDefault("source", "").toString().toLowerCase();
                        return source.contains(activePdfKey);
                    })
                    .collect(java.util.stream.Collectors.toList());
            if (!filtered.isEmpty()) {
                candidateDocs = filtered;
            }
        }

        if (query == null || query.trim().isEmpty()) {
            return candidateDocs.stream().limit(limit).collect(java.util.stream.Collectors.toList());
        }

        String[] queryTokens = query.toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .split("\\s+");

        List<String> validTokens = java.util.Arrays.stream(queryTokens)
                .filter(t -> t.length() > 2)
                .filter(t -> !STOP_WORDS.contains(t))
                .collect(java.util.stream.Collectors.toList());

        if (validTokens.isEmpty()) {
            // If a specific scheme was targeted, return documents for that scheme. Otherwise return top single doc.
            if (activePdfKey != null) {
                return candidateDocs.stream().limit(limit).collect(java.util.stream.Collectors.toList());
            } else {
                return candidateDocs.isEmpty() ? new ArrayList<>() : java.util.Collections.singletonList(candidateDocs.get(0));
            }
        }

        Map<Document, Integer> scores = new HashMap<>();
        for (Document doc : candidateDocs) {
            String contentLower = doc.getContent().toLowerCase();
            String sourceLower = doc.getMetadata().getOrDefault("source", "").toString().toLowerCase();
            int score = 0;
            for (String token : validTokens) {
                if (sourceLower.contains(token)) {
                    score += 15;
                }
                int count = countOccurrences(contentLower, token);
                score += count * 3;
            }
            if (score > 0) {
                scores.put(doc, score);
            }
        }

        if (scores.isEmpty()) {
            if (activePdfKey != null) {
                return candidateDocs.stream().limit(limit).collect(java.util.stream.Collectors.toList());
            } else {
                return candidateDocs.isEmpty() ? new ArrayList<>() : java.util.Collections.singletonList(candidateDocs.get(0));
            }
        }

        List<Document> scoredDocs = scores.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toList());

        // If no scheme was explicitly selected, isolate results to the single best matching scheme (file)
        if (activePdfKey == null && !scoredDocs.isEmpty()) {
            String topSource = scoredDocs.get(0).getMetadata().getOrDefault("source", "").toString();
            scoredDocs = scoredDocs.stream()
                    .filter(doc -> doc.getMetadata().getOrDefault("source", "").toString().equals(topSource))
                    .collect(java.util.stream.Collectors.toList());
        }

        return scoredDocs.stream().limit(limit).collect(java.util.stream.Collectors.toList());
    }

    private String resolvePdfKeyword(String input) {
        if (input == null || input.trim().isEmpty()) return null;
        String lower = input.toLowerCase();
        if (lower.contains("kisan")) return "pmkisan";
        if (lower.contains("pmay") || lower.contains("awas")) return "pmay";
        if (lower.contains("sukanya") || lower.contains("ssy")) return "ssy";
        if (lower.contains("bhamashah") || lower.contains("swasthya")) return "bhamashah";
        if (lower.contains("widow") || lower.contains("pension")) return "widow_pension";
        if (lower.contains("sc_scholarship") || lower.contains("post matric") || (lower.contains("sc") && lower.contains("scholarship"))) return "sc_scholarship";
        if (lower.contains("nmmss") || lower.contains("merit-cum-means")) return "nmmss";
        if (lower.contains("yuva") || lower.contains("sambal")) return "yuva_sambal";
        if (lower.contains("beti") || lower.contains("bbbp")) return "beti_bachao";
        if (lower.contains("mahila") || lower.contains("samman")) return "mahila_samman";
        return null;
    }

    private int countOccurrences(String text, String sub) {
        int count = 0, idx = 0;
        while ((idx = text.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    private void ingestPdf(File pdfFile, String originalFileName) {
        List<Document> springAiDocs = new ArrayList<>();

        // Load PDF page-by-page to attach source citations (Page Numbers)
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            int totalPages = document.getNumberOfPages();
            PDFTextStripper stripper = new PDFTextStripper();

            for (int page = 1; page <= totalPages; page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                String text = stripper.getText(document);

                if (text != null && !text.trim().isEmpty()) {
                    Map<String, Object> metadata = new HashMap<>();
                    metadata.put("source", originalFileName);
                    metadata.put("page", page);
                    metadata.put("uploadedAt", LocalDateTime.now().toString());

                    Document doc = new Document(text, metadata);
                    springAiDocs.add(doc);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to read PDF page-by-page for " + originalFileName + ": " + e.getMessage());
            return;
        }

        if (springAiDocs.isEmpty()) {
            return;
        }

        // Add to internal fallback registry (deduplicating by source and page)
        for (Document d : springAiDocs) {
            boolean exists = documentRegistry.stream().anyMatch(r ->
                    d.getMetadata().getOrDefault("source", "").equals(r.getMetadata().getOrDefault("source", "")) &&
                    d.getMetadata().getOrDefault("page", -1).equals(r.getMetadata().getOrDefault("page", -2)) &&
                    d.getContent().equals(r.getContent())
            );
            if (!exists) {
                documentRegistry.add(d);
            }
        }

        // Split text into chunks using Spring AI TokenTextSplitter
        try {
            TokenTextSplitter textSplitter = new TokenTextSplitter();
            List<Document> splitDocs = textSplitter.split(springAiDocs);

            // Add to Vector Store (generates embeddings and saves)
            vectorStore.accept(splitDocs);

            // Persist SimpleVectorStore to file if applicable
            if (vectorStore instanceof SimpleVectorStore) {
                ((SimpleVectorStore) vectorStore).save(new File(vectorStorePath));
            }
        } catch (Exception e) {
            System.err.println("VectorStore ingest warning for " + originalFileName + " (using fallback registry): " + e.getMessage());
        }
    }
}
