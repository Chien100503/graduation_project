package com.petshop.petopia.service;

import com.petshop.petopia.dto.request.cart.CartItemRequest;
import com.petshop.petopia.dto.request.cart.CartItemUpdateRequest;
import com.petshop.petopia.dto.response.cart.CartResponse;
import com.petshop.petopia.model.User;
import com.petshop.petopia.model.cart.Cart;
import com.petshop.petopia.model.cart.CartItem;
import com.petshop.petopia.model.product.Pet;
import com.petshop.petopia.model.product.Product;
import com.petshop.petopia.repository.cart.CartItemRepository;
import com.petshop.petopia.repository.cart.CartRepository;
import com.petshop.petopia.repository.product.PetRepository;
import com.petshop.petopia.repository.product.ProductRepository;
import com.petshop.petopia.repository.user.UserRepository;
import com.petshop.petopia.util.ConvertCart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final ConvertCart convert;

    @Transactional
    public CartResponse addToCart(Integer userId, CartItemRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));
        Cart cart = getOrCreateCart(user);

        if (request.getPetId() != null) {
            addPetToCart(cart, request.getPetId());
        } else if (request.getProductId() != null) {
            addProductToCart(cart, request.getProductId(), request.getQuantity());
        } else {
            throw new IllegalArgumentException("Yêu cầu thêm vào giỏ hàng không hợp lệ.");
        }
        recalculateCartTotalPrice(cart);
        return convert.toCartResponse(cart);
    }

    @Transactional
    public CartResponse updateCartItem(Integer userId, CartItemUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));
        Cart cart = getOrCreateCart(user);
        CartItem item = cartItemRepository.findById(request.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy item trong giỏ hàng với ID: " + request.getItemId()));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Item không thuộc về giỏ hàng của người dùng này.");
        }

        if (request.getQuantity() != null) {
            if (item.getProduct() != null) {
                // Là sản phẩm
                Product product = item.getProduct();
                if (request.getQuantity() <= 0) {
                    cartItemRepository.delete(item);
                    cart.getItems().remove(item);
                } else if (product.getStockQuantity() < request.getQuantity()) {
                    throw new IllegalArgumentException("Không đủ số lượng sản phẩm trong kho.");
                } else {
                    item.setQuantity(request.getQuantity());
                    item.setItemTotalPrice(item.getPrice() * item.getQuantity());
                    cartItemRepository.save(item);
                }
            } else if (item.getPet() != null) {
                // Là thú cưng
                if (request.getQuantity() <= 0) {
                    cartItemRepository.delete(item);
                    cart.getItems().remove(item);
                } else if (request.getQuantity() >= 2) {
                    throw new IllegalArgumentException("Số lượng thú cưng không được lớn hơn 1.");
                } else {
                    item.setQuantity(request.getQuantity());
                    item.setItemTotalPrice(item.getPrice() * item.getQuantity());
                    cartItemRepository.save(item);
                }
            }
            recalculateCartTotalPrice(cart);
        }
        return convert.toCartResponse(cart);
    }

    @Transactional
    public CartResponse getCart(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));
        Cart cart = getOrCreateCart(user);
        recalculateCartTotalPrice(cart);
        return convert.toCartResponse(cart);
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setTotalPrice(0.0); // **Khởi tạo totalPrice là 0.0**
            return cartRepository.save(newCart);
        });
    }

    private void addPetToCart(Cart cart, Integer petId) {
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thú cưng với ID: " + petId));

        CartItem existingItem = cartItemRepository.findByCartAndPet(cart, pet).orElse(null);
        if (existingItem == null) {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setPet(pet);
            newItem.setProduct(null);
            newItem.setQuantity(1);
            newItem.setPrice(pet.getPrice());
            newItem.setItemTotalPrice(pet.getPrice()); // Tính itemTotalPrice
            cartItemRepository.save(newItem);
            cart.getItems().add(newItem);
        }
        // Nếu đã tồn tại, không thêm mới (tùy theo logic nghiệp vụ bạn có thể tăng số lượng)
    }

    private void addProductToCart(Cart cart, Integer productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Số lượng sản phẩm không hợp lệ.");
        }

        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + productId));

        CartItem existingItem = cartItemRepository.findByCartAndProduct(cart, product).orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setItemTotalPrice(existingItem.getPrice() * existingItem.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setPet(null);
            newItem.setQuantity(quantity);
            newItem.setPrice(product.getPrice());
            newItem.setItemTotalPrice(product.getPrice() * quantity); // Tính itemTotalPrice
            cartItemRepository.save(newItem);
            cart.getItems().add(newItem);
        }
    }

    private void recalculateCartTotalPrice(Cart cart) {
        double totalPrice = 0.0;
        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                totalPrice += (item.getItemTotalPrice() != null ? item.getItemTotalPrice() : 0.0);
            }
        }
        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);
    }
}