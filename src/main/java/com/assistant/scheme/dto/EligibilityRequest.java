package com.assistant.scheme.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EligibilityRequest {
    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be positive")
    private Integer age;

    @NotNull(message = "Annual income is required")
    @Min(value = 0, message = "Income must be positive")
    private Double income;

    @NotBlank(message = "Occupation is required")
    private String occupation;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Gender is required")
    private String gender;

    // Default Constructor
    public EligibilityRequest() {}

    // Getters and Setters
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Double getIncome() { return income; }
    public void setIncome(Double income) { this.income = income; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
