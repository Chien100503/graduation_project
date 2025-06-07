package com.petshop.petopia.model.cart;

import com.petshop.petopia.component.Global;
import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Cart cart;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Pet pet;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private Global.ItemType itemType;

    private Integer quantity;

    @AssertTrue(message = "Một mục giỏ hàng chỉ có thể chứa pet hoặc product, không cả hai.")
    private boolean isPetOrProduct() {
        return (pet != null && product == null) || (pet == null && product != null);
    }
}