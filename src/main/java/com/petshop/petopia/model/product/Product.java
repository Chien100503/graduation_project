package com.petshop.petopia.model.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "pr_category_id")
    private ProductCategory prCategory;

    @ManyToOne
    @JoinColumn(name = "brand_id") // Liên kết với bảng Brand
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "type_id")   // Liên kết với bảng Type
    private Type type;

    private String description;
    private Integer price;
    private Integer stockQuantity;
    private Integer size;
    private Double weight;
    private String expirationDate;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true) // Quan hệ với bảng ProductImage
    private List<ProductImage> productImages;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = new Date();
    }
}