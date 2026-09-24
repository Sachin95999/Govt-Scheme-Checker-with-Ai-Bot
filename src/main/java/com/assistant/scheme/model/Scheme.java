package com.assistant.scheme.model;

import jakarta.persistence.*;

@Entity
@Table(name = "schemes")
public class Scheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String department;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "min_age")
    private Integer minAge;

    @Column(name = "max_age")
    private Integer maxAge;

    @Column(name = "max_income")
    private Double maxIncome;

    @Column(name = "eligible_occupations", columnDefinition = "TEXT")
    private String eligibleOccupations;

    @Column(name = "eligible_categories", columnDefinition = "TEXT")
    private String eligibleCategories;

    @Column(name = "eligible_genders")
    private String eligibleGenders;

    @Column(name = "documents_required", columnDefinition = "TEXT")
    private String documentsRequired;

    @Column(name = "apply_link")
    private String applyLink;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    // Constructors
    public Scheme() {}

    public Scheme(Long id, String name, String category, String state, String department, String description,
                  Integer minAge, Integer maxAge, Double maxIncome, String eligibleOccupations,
                  String eligibleCategories, String eligibleGenders, String documentsRequired,
                  String applyLink, String pdfUrl, String metadata) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.state = state;
        this.department = department;
        this.description = description;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.maxIncome = maxIncome;
        this.eligibleOccupations = eligibleOccupations;
        this.eligibleCategories = eligibleCategories;
        this.eligibleGenders = eligibleGenders;
        this.documentsRequired = documentsRequired;
        this.applyLink = applyLink;
        this.pdfUrl = pdfUrl;
        this.metadata = metadata;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getMinAge() { return minAge; }
    public void setMinAge(Integer minAge) { this.minAge = minAge; }

    public Integer getMaxAge() { return maxAge; }
    public void setMaxAge(Integer maxAge) { this.maxAge = maxAge; }

    public Double getMaxIncome() { return maxIncome; }
    public void setMaxIncome(Double maxIncome) { this.maxIncome = maxIncome; }

    public String getEligibleOccupations() { return eligibleOccupations; }
    public void setEligibleOccupations(String eligibleOccupations) { this.eligibleOccupations = eligibleOccupations; }

    public String getEligibleCategories() { return eligibleCategories; }
    public void setEligibleCategories(String eligibleCategories) { this.eligibleCategories = eligibleCategories; }

    public String getEligibleGenders() { return eligibleGenders; }
    public void setEligibleGenders(String eligibleGenders) { this.eligibleGenders = eligibleGenders; }

    public String getDocumentsRequired() { return documentsRequired; }
    public void setDocumentsRequired(String documentsRequired) { this.documentsRequired = documentsRequired; }

    public String getApplyLink() { return applyLink; }
    public void setApplyLink(String applyLink) { this.applyLink = applyLink; }

    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    // Static Builder
    public static SchemeBuilder builder() {
        return new SchemeBuilder();
    }

    public static class SchemeBuilder {
        private Long id;
        private String name;
        private String category;
        private String state;
        private String department;
        private String description;
        private Integer minAge;
        private Integer maxAge;
        private Double maxIncome;
        private String eligibleOccupations;
        private String eligibleCategories;
        private String eligibleGenders;
        private String documentsRequired;
        private String applyLink;
        private String pdfUrl;
        private String metadata;

        public SchemeBuilder id(Long id) { this.id = id; return this; }
        public SchemeBuilder name(String name) { this.name = name; return this; }
        public SchemeBuilder category(String category) { this.category = category; return this; }
        public SchemeBuilder state(String state) { this.state = state; return this; }
        public SchemeBuilder department(String department) { this.department = department; return this; }
        public SchemeBuilder description(String description) { this.description = description; return this; }
        public SchemeBuilder minAge(Integer minAge) { this.minAge = minAge; return this; }
        public SchemeBuilder maxAge(Integer maxAge) { this.maxAge = maxAge; return this; }
        public SchemeBuilder maxIncome(Double maxIncome) { this.maxIncome = maxIncome; return this; }
        public SchemeBuilder eligibleOccupations(String eligibleOccupations) { this.eligibleOccupations = eligibleOccupations; return this; }
        public SchemeBuilder eligibleCategories(String eligibleCategories) { this.eligibleCategories = eligibleCategories; return this; }
        public SchemeBuilder eligibleGenders(String eligibleGenders) { this.eligibleGenders = eligibleGenders; return this; }
        public SchemeBuilder documentsRequired(String documentsRequired) { this.documentsRequired = documentsRequired; return this; }
        public SchemeBuilder applyLink(String applyLink) { this.applyLink = applyLink; return this; }
        public SchemeBuilder pdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; return this; }
        public SchemeBuilder metadata(String metadata) { this.metadata = metadata; return this; }

        public Scheme build() {
            return new Scheme(id, name, category, state, department, description, minAge, maxAge, maxIncome,
                    eligibleOccupations, eligibleCategories, eligibleGenders, documentsRequired, applyLink, pdfUrl, metadata);
        }
    }
}
