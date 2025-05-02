package com.petshop.petopia.model.order;

import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "order_item")
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
    private Integer price;

    @Transient
    private String imageUrl;

    @Transient
    private Integer itemTotalPrice;

    @PostLoad
    private void onLoad() {
        if (product != null) {
            this.imageUrl = (product.getProductImages() != null && !product.getProductImages().isEmpty())
                    ? product.getProductImages().get(0).getImageUrl()
                    : null;
        } else if (pet != null) {
            this.imageUrl = (pet.getPetImages() != null && !pet.getPetImages().isEmpty())
                    ? pet.getPetImages().get(0).getImageUrl()
                    : null;
        }
        this.itemTotalPrice = this.price * this.quantity;
    }
}
