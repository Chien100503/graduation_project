package com.petshop.petopia.model;

import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.product.Product;
import com.petshop.petopia.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "reviews")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    private Integer rating;
    private String comment;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}