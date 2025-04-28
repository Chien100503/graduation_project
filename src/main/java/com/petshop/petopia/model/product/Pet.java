package com.petshop.petopia.model.product;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "pet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "pet_category_id") // Sửa tên cột khóa ngoại
    private PetCategory petCategory; // Sửa tên trường để khớp với PetCategory

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