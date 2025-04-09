package com.petshop.petopia.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "product_categories")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer prCategoryId;

    private String name;
    private String description;

    @OneToMany(mappedBy = "prCategory")
    private List<Product> products;

    @OneToMany(mappedBy = "prCategory")
    private List<Pet> pets;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}