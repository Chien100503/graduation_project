package com.petshop.petopia.model.product;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "pets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pid;

    @ManyToOne
    @JoinColumn(name = "product_category_id")
    private ProductCategory prCategory;

    private String name;
    private String breed;
    private Integer age;
    private String gender;
    private String size;
    private Double weight;
    private String color;
    private Double price;
    private Boolean status;
    private Boolean healthStatus;
    private String description;
    private String img;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = new Date();
    }
}
