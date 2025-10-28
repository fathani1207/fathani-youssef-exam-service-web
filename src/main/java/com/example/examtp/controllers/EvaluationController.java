package com.example.examtp.controllers;


import com.example.examtp.dto.evaluation.create.CreateEvaluationDto;
import com.example.examtp.dto.evaluation.read.EvaluationDto;
import com.example.examtp.services.authentication.JwtExtractorService;
import com.example.examtp.services.evaluation.EvaluationServices;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {
    private final EvaluationServices evaluationServices;
    private final JwtExtractorService jwtExtractorService;

    @Autowired
    public EvaluationController(EvaluationServices evaluationServices, JwtExtractorService jwtExtractorService) {
        this.evaluationServices = evaluationServices;
        this.jwtExtractorService = jwtExtractorService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<EvaluationDto> getEvaluations(@RequestParam(required = false) String keyword, @RequestParam(required = false, defaultValue = "10") int maxResults) {
        if (keyword != null && !keyword.isBlank()) {
            return this.evaluationServices.getEvaluationsByKeyword(keyword, maxResults);
        } else {
            return this.evaluationServices.getAllEvaluations();
        }
    }

    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<EvaluationDto>> getMyEvaluations(Authentication authentication) {
        String username = jwtExtractorService.getPreferredUsername(authentication);
        return ResponseEntity.ok(this.evaluationServices.getMyEvaluations(username));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<EvaluationDto> createEvaluation(@Valid @ModelAttribute CreateEvaluationDto evaluationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.evaluationServices.createEvaluation(evaluationDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvaluation(@PathVariable Long id) {
        this.evaluationServices.deleteEvaluation(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/mine/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Void> deleteMyEvaluation(@PathVariable long id, String name) {
        this.evaluationServices.deleteMyEvaluation(id, name);
        return ResponseEntity.noContent().build();
    }
}
