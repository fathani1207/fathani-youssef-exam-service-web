package com.example.examtp.repositories;

import com.example.examtp.entities.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    Optional<List<Evaluation>> findByAuthor(String name);

    Optional<Evaluation> findByAuthorAndId(String author, long id);
}
