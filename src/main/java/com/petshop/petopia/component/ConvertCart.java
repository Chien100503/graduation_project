// File: ConvertCart.java
package com.petshop.petopia.component;

import com.petshop.petopia.dto.response.cart.CartResponse;
import com.petshop.petopia.dto.response.cart.CartItemResponse;
import com.petshop.petopia.model.cart.Cart;
import com.petshop.petopia.model.cart.CartItem;
import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ConvertCart {
    private final CalculatePrice calculatePrice;

    public CartResponse toCartResponse(Cart cart) {
        if (cart == null) {
            return null;
        }

        List<CartItemResponse> cartItemResponses = new ArrayList<>();
        BigDecimal totalCartPrice = BigDecimal.ZERO;

        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                CartItemResponse itemResponse = toCartItemResponse(item);
                cartItemResponses.add(itemResponse);
                if (itemResponse.getItemTotalPrice() != null) {
                    totalCartPrice = totalCartPrice.add(itemResponse.getItemTotalPrice());
                }
            }
        }

        return CartResponse.builder()
                .id(cart.getId())
                .items(cartItemResponses)
                .totalPrice(totalCartPrice.setScale(2, RoundingMode.HALF_UP))
                .build();
    }

    public CartItemResponse toCartItemResponse(CartItem cartItem) {
        CartItemResponse cartItemResponse = new CartItemResponse();
        cartItemResponse.setId(cartItem.getId());
        cartItemResponse.setItemType(cartItem.getItemType());

        BigDecimal originalPrice = BigDecimal.ZERO;
        BigDecimal salePercent = BigDecimal.ZERO;
        String name = null;
        Integer quantity = null;

        if (cartItem.getItemType() == Global.ItemType.PRODUCT) {
            Product product = cartItem.getProduct();
            if (product != null) {
                cartItemResponse.setProductId(product.getId());
                cartItemResponse.setProductName(product.getName());
                cartItemResponse.setThumbnailUrl(product.getThumbnail());
                originalPrice = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
                salePercent = (product.getBanner() != null && product.getBanner().getSalePercent() != null) ?
                        product.getBanner().getSalePercent() : BigDecimal.ZERO;
                quantity = cartItem.getQuantity();

                if (product.getBrand() != null) {
                    cartItemResponse.setBrandName(product.getBrand().getName());
                }
            }
        } else if (cartItem.getItemType() == Global.ItemType.PET) {
            Pet pet = cartItem.getPet();
            if (pet != null) {
                cartItemResponse.setPetId(pet.getId());
                cartItemResponse.setPetName(pet.getName());
                cartItemResponse.setThumbnailUrl(pet.getThumbnail());
                originalPrice = pet.getPrice() != null ? pet.getPrice() : BigDecimal.ZERO;
                salePercent = (pet.getBanner() != null && pet.getBanner().getSalePercent() != null) ?
                        pet.getBanner().getSalePercent() : BigDecimal.ZERO;

                if (pet.getBreed() != null) {
                    cartItemResponse.setBreedName(pet.getBreed().getName());
                }
            }
        }

        cartItemResponse.setQuantity(quantity);

        BigDecimal priceDiscount = calculatePrice.calculatePriceDiscount(originalPrice, salePercent); // Đổi thành calculateFinalPrice nếu bạn đã sửa trong CalculateDiscount
        BigDecimal itemTotalPrice = priceDiscount.multiply(BigDecimal.valueOf(cartItem.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP);

        cartItemResponse.setPrice(originalPrice.setScale(2, RoundingMode.HALF_UP));
        cartItemResponse.setPriceDiscount(priceDiscount.setScale(2, RoundingMode.HALF_UP));
        cartItemResponse.setItemTotalPrice(itemTotalPrice);

        return cartItemResponse;
    }
}