package com.petshop.petopia.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "products")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pid;

    private String name;

    @ManyToOne
    @JoinColumn(name = "pr_category_id")
    private ProductCategory prCategory;

    private String brand;
    private String type;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private Integer size;
    private Double weight;
    private Date expirationDate;
    private String images;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}