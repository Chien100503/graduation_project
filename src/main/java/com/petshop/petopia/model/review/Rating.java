package com.petshop.petopia.model.review;

import com.petshop.petopia.model.product.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "ratings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "product_id", unique = true)
    private Product product;

    private Integer oneStarCount = 0;
    private Integer twoStarCount = 0;
    private Integer threeStarCount = 0;
    private Integer fourStarCount = 0;
    private Integer fiveStarCount = 0;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
         this.updatedAt = new Date();
    }
}