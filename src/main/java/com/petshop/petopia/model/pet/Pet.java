package com.petshop.petopia.model.pet;

import com.petshop.petopia.component.Global;
import com.petshop.petopia.model.sale.Banner;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
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

    private Global.ItemType itemType = Global.ItemType.PET;

    private String name;
    private Integer age;
    private String gender;
    private String size;
    private Double weight;
    private String color;
    private BigDecimal price;
    private Boolean status;
    private String thumbnail;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetImage> petImages;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banner_id")
    private Banner banner;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = new Date();
    }
}