package com.example.examtp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


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

    @Column(name = "restaurant_image_url", length = 500)
    private String restaurantImageUrl;
}
