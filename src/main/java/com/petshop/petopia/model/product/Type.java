package com.petshop.petopia.model.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Type {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_category_id", nullable = false)
    private ProductCategory productCategory;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = new Date();
    }
}