package com.petshop.petopia.model.pet;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "pet_categories")
@Getter
@Setter
public class PetCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = new Date();
    }
}