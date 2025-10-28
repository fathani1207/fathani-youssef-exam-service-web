package com.example.examtp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;
import java.util.ArrayList;


@Entity
@Table(name = "restaurants")
@Getter
@Setter
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", length = 90)
    private String name;

    @Column(name = "address", length = 255)
    private String address;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Evaluation> evaluations;

    @Column(name = "average_rating")
    @ColumnDefault("-1")
    private double averageRating;

    @Column(name = "restaurant_image_url", length = 500)
    private String restaurantImageUrl;

    @PrePersist
    public void prePersist() {
        this.averageRating = -1;
    }

    public void addEvaluation(Evaluation evaluation) {
        if (evaluations == null) {
            evaluations = new ArrayList<>();
        }
        this.evaluations.add(evaluation);

        double average = evaluations.stream()
                .mapToDouble(Evaluation::getNote)
                .average()
                .orElse(-1);

        setAverageRating(average);
    }
}
