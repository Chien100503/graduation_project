package com.petshop.petopia.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "pet_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String description;

    @OneToMany(mappedBy = "petCategory")
    @JsonIgnore
    private List<Pet> pets;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}