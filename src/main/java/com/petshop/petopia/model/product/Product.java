package com.petshop.petopia.model.product;

import com.fasterxml.jackson.databind.JsonNode;
import com.petshop.petopia.model.ItemType;
import com.petshop.petopia.model.review.ProductRating;
import com.petshop.petopia.model.review.Review;
import com.petshop.petopia.model.sale.Banner;
import com.petshop.petopia.component.JsonNodeConverter;
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
    @JoinColumn(name = "brand_id")
    private Brand brand;

    private ItemType itemType = ItemType.PRODUCT;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private Type type;

    private String description;
    private Integer price;
    private Integer stockQuantity;
    private Integer size;
    private Double weight;
    private String expirationDate;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @OneToMany(mappedBy = "product")
    private List<Review> reviews;

    @OneToMany(mappedBy = "product")
    private List<ProductRating> productRatings;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banner_id")
    private Banner banner;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "total_ratings")
    private Integer totalRatings = 0;

    @Convert(converter = JsonNodeConverter.class)
    @Column(name = "rating_counts", columnDefinition = "JSON")
    private JsonNode ratingCounts;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = new Date();
    }
}