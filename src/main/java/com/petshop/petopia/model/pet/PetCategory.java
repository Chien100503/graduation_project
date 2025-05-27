package com.petshop.petopia.model.pet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "pet_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String imageUrl;

    @OneToMany(mappedBy = "petCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Breed> breeds;

    @OneToMany(mappedBy = "petCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Pet> pets;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = new Date();
    }
}