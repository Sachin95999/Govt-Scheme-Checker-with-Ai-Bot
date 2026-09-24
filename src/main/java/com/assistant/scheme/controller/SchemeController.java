package com.assistant.scheme.controller;

import com.assistant.scheme.dto.EligibilityRequest;
import com.assistant.scheme.model.Scheme;
import com.assistant.scheme.service.SchemeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/schemes")
public class SchemeController {

    private final SchemeService schemeService;

    @Autowired
    public SchemeController(SchemeService schemeService) {
        this.schemeService = schemeService;
    }

    @GetMapping
    public ResponseEntity<List<Scheme>> searchSchemes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String department) {
        
        List<Scheme> schemes = schemeService.searchSchemes(name, category, state, department);
        return ResponseEntity.ok(schemes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Scheme> getSchemeById(@PathVariable Long id) {
        Scheme scheme = schemeService.getSchemeById(id);
        if (scheme == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(scheme);
    }

    @PostMapping("/check-eligibility")
    public ResponseEntity<List<Scheme>> checkEligibility(@Valid @RequestBody EligibilityRequest request) {
        List<Scheme> eligibleSchemes = schemeService.checkEligibility(
                request.getAge(),
                request.getIncome(),
                request.getOccupation(),
                request.getState(),
                request.getCategory(),
                request.getGender()
        );
        return ResponseEntity.ok(eligibleSchemes);
    }
}
