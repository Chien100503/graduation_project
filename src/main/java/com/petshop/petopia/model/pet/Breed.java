package com.petshop.petopia.model.pet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "breeds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Breed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "pet_category_id")
    private PetCategory petCategory;

    @OneToMany(mappedBy = "breed", cascade = CascadeType.ALL, orphanRemoval = true)
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