package com.example.examtp.controllers;


import com.example.examtp.dto.evaluation.create.CreateEvaluationDto;
import com.example.examtp.dto.evaluation.read.EvaluationDto;
import com.example.examtp.services.EvaluationServices;
import jakarta.validation.Valid;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluations")
public class EvaluationController {
    private final EvaluationServices evaluationServices;

    @Autowired
    public EvaluationController(EvaluationServices evaluationServices) {
        this.evaluationServices = evaluationServices;
    }

    @GetMapping
    public List<EvaluationDto> getEvaluations(@RequestParam(required = false) String keyword, @RequestParam(required = false, defaultValue = "10") int maxResults) {
        if (keyword != null && !keyword.isEmpty()) {
            return this.evaluationServices.getEvaluationsByKeyword(keyword, maxResults);
        } else {
            return this.evaluationServices.getAllEvaluations();
        }
    }

    @GetMapping("/mine")
    public List<EvaluationDto> getMyEvaluations(String name) {
        return this.evaluationServices.getMyEvaluations(name);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EvaluationDto> createEvaluation(@Valid @ModelAttribute CreateEvaluationDto evaluationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.evaluationServices.createEvaluation(evaluationDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvaluation(@PathVariable Long id) {
        this.evaluationServices.deleteEvaluation(id);
        return ResponseEntity.noContent().build();
    }
}
