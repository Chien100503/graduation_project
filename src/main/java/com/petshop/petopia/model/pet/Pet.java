package com.petshop.petopia.model.pet;

import com.petshop.petopia.model.ItemType;
import com.petshop.petopia.model.sale.Banner;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "pets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "pet_category_id")
    private PetCategory petCategory;

    @ManyToOne
    @JoinColumn(name = "breed_id")
    private Breed breed;

    private ItemType itemType = ItemType.PET;

    private String name;
    private Integer age;
    private String gender;
    private String size;
    private Double weight;
    private String color;
    private Integer price;
    private Boolean status;
    private String description;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetImage> petImages;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banner_id") // Khóa ngoại trỏ đến Banner
    private Banner banner;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = new Date();
    }

    @PrePersist
    public void setCreatedAt() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
}