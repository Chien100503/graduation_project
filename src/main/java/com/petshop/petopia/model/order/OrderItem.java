package com.petshop.petopia.model.order;

import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Order order;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Pet pet;

    private Integer quantity;


    @Transient
    private String imageUrl;

    private BigDecimal priceDiscount;
    private BigDecimal price;
    @Transient
    private BigDecimal itemTotalPrice;

    @PostLoad
    private void onLoad() {
        imageUrl = (product != null && product.getProductImages() != null && !product.getProductImages().isEmpty())
                ? product.getProductImages().getFirst().getImageUrl()
                : (pet != null && pet.getPetImages() != null && !pet.getPetImages().isEmpty())
                ? pet.getPetImages().getFirst().getImageUrl()
                : null;
        itemTotalPrice = price != null && quantity != null ? price.multiply(BigDecimal.valueOf(quantity)) : BigDecimal.ZERO;
    }
}
