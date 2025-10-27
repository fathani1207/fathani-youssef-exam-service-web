package com.example.examtp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name = "evaluations")
@Getter
@Setter
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "author", length = 50)
    private String author;

    @Column(name = "content", length = 255)
    private String content;

    @Column(name = "note")
    private int note;

    @ElementCollection
    @CollectionTable(name = "evaluation_images", joinColumns = @JoinColumn(name = "evaluation_id"))
    @Column(name = "evaluation_image_url", length = 500)
    private List<String> evaluationImagesUrls;

    @ManyToOne(fetch = FetchType.LAZY, cascade =  {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        restaurant.addEvaluation(this);
    }
}
