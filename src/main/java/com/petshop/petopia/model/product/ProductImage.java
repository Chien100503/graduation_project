package com.petshop.petopia.model.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "product_images") // Bảng để lưu trữ hình ảnh sản phẩm
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id") // Liên kết với bảng Product
    private Product product;

    private String imageUrl; // URL của hình ảnh

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = new Date();
    }
}