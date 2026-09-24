package com.assistant.scheme.service;

import com.assistant.scheme.model.Scheme;
import com.assistant.scheme.repository.SchemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchemeService {

    private final SchemeRepository schemeRepository;

    @Autowired
    public SchemeService(SchemeRepository schemeRepository) {
        this.schemeRepository = schemeRepository;
    }

    public Scheme saveScheme(Scheme scheme) {
        return schemeRepository.save(scheme);
    }

    public List<Scheme> getAllSchemes() {
        return schemeRepository.findAll();
    }

    public Scheme getSchemeById(Long id) {
        return schemeRepository.findById(id).orElse(null);
    }

    public void deleteScheme(Long id) {
        schemeRepository.deleteById(id);
    }

    public List<Scheme> searchSchemes(String name, String category, String state, String department) {
        return schemeRepository.searchSchemes(
                name != null && !name.trim().isEmpty() ? name : null,
                category != null && !category.trim().isEmpty() ? category : null,
                state != null && !state.trim().isEmpty() ? state : null,
                department != null && !department.trim().isEmpty() ? department : null
        );
    }

    public List<Scheme> checkEligibility(int age, double income, String occupation, String state, String category, String gender) {
        List<Scheme> allSchemes = schemeRepository.findAll();

        return allSchemes.stream().filter(scheme -> {
            // 1. Age check
            if (scheme.getMinAge() != null && age < scheme.getMinAge()) {
                return false;
            }
            if (scheme.getMaxAge() != null && age > scheme.getMaxAge()) {
                return false;
            }

            // 2. Income check
            if (scheme.getMaxIncome() != null && income > scheme.getMaxIncome()) {
                return false;
            }

            // 3. State check
            if (state != null && !state.equalsIgnoreCase("All") && !state.equalsIgnoreCase("Any")) {
                if (scheme.getState() != null &&
                        !scheme.getState().equalsIgnoreCase("Central") &&
                        !scheme.getState().equalsIgnoreCase("All") &&
                        !scheme.getState().equalsIgnoreCase(state)) {
                    return false;
                }
            }

            // 4. Gender check
            if (gender != null && !gender.equalsIgnoreCase("All") && !gender.equalsIgnoreCase("Any")) {
                if (scheme.getEligibleGenders() != null &&
                        !scheme.getEligibleGenders().equalsIgnoreCase("All") &&
                        !scheme.getEligibleGenders().equalsIgnoreCase(gender)) {
                    return false;
                }
            }

            // 5. Category check (comma-separated list)
            if (category != null && !category.equalsIgnoreCase("All") && !category.equalsIgnoreCase("Any")) {
                if (scheme.getEligibleCategories() != null &&
                        !scheme.getEligibleCategories().equalsIgnoreCase("All") &&
                        !scheme.getEligibleCategories().trim().isEmpty()) {
                    List<String> categories = Arrays.stream(scheme.getEligibleCategories().split(","))
                            .map(String::trim)
                            .map(String::toLowerCase)
                            .collect(Collectors.toList());
                    if (!categories.contains(category.toLowerCase())) {
                        return false;
                    }
                }
            }

            // 6. Occupation check (comma-separated list)
            if (occupation != null && !occupation.equalsIgnoreCase("All") && !occupation.equalsIgnoreCase("Any")) {
                if (scheme.getEligibleOccupations() != null &&
                        !scheme.getEligibleOccupations().equalsIgnoreCase("All") &&
                        !scheme.getEligibleOccupations().trim().isEmpty()) {
                    List<String> occupations = Arrays.stream(scheme.getEligibleOccupations().split(","))
                            .map(String::trim)
                            .map(String::toLowerCase)
                            .collect(Collectors.toList());
                    if (!occupations.contains(occupation.toLowerCase())) {
                        return false;
                    }
                }
            }

            return true;
        }).collect(Collectors.toList());
    }
}
