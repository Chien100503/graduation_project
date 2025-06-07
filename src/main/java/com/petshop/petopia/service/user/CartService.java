package com.petshop.petopia.service.user;

import com.petshop.petopia.component.Global;
import com.petshop.petopia.dto.request.cart.CartItemRequest;
import com.petshop.petopia.dto.request.cart.CartItemUpdateRequest;
import com.petshop.petopia.dto.response.cart.CartResponse;
import com.petshop.petopia.model.cart.Cart;
import com.petshop.petopia.model.cart.CartItem;
import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.product.Product;
import com.petshop.petopia.model.user.User;
import com.petshop.petopia.repository.cart.CartItemRepository;
import com.petshop.petopia.repository.cart.CartRepository;
import com.petshop.petopia.repository.pet.PetRepository;
import com.petshop.petopia.repository.product.ProductRepository;
import com.petshop.petopia.repository.user.UserRepository;
import com.petshop.petopia.component.ConvertCart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final ConvertCart convert; // ConvertCart sẽ lo việc tính toán giá

    @Transactional
    public CartResponse addToCart(Integer userId, CartItemRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));
        Cart cart = getOrCreateCart(user);

        if (request == null) {
            throw new IllegalArgumentException("Yêu cầu thêm vào giỏ hàng không hợp lệ: Request không được null.");
        }

        if (request.getPetId() != null && request.getProductId() == null) {
            addPetToCart(cart, request.getPetId());
        } else if (request.getProductId() != null && request.getPetId() == null) {
            addProductToCart(cart, request.getProductId(), request.getQuantity());
        } else {
            throw new IllegalArgumentException("Yêu cầu thêm vào giỏ hàng không hợp lệ. Chỉ cung cấp PetId hoặc ProductId.");
        }
        cartRepository.save(cart);

        return convert.toCartResponse(cart);
    }

    @Transactional
    public CartResponse updateCartItem(Integer userId, CartItemUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));

        Cart cart = getOrCreateCart(user);

        Optional<CartItem> itemOpt = cart.getItems().stream()
                .filter(item -> item.getId().equals(request.getItemId()))
                .findFirst();

        CartItem item = itemOpt.orElseGet(() -> cartItemRepository.findByIdAndCart(request.getItemId(), cart)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy item trong giỏ hàng của người dùng với ID: " + request.getItemId())));

        if (request.getQuantity() != null) {
            if (item.getItemType() == Global.ItemType.PRODUCT) {
                Product product = item.getProduct();
                if (request.getQuantity() <= 0) {
                    cart.getItems().remove(item);
                    cartItemRepository.delete(item);
                } else if (product.getStockQuantity() == null || product.getStockQuantity() < request.getQuantity()) {
                    throw new IllegalArgumentException("Không đủ số lượng sản phẩm trong kho. Chỉ còn " + (product.getStockQuantity() != null ? product.getStockQuantity() : 0) + " sản phẩm.");
                } else {
                    item.setQuantity(request.getQuantity()); // Cập nhật số lượng
                    cartItemRepository.save(item);
                }
            } else if (item.getItemType() == Global.ItemType.PET) {
                if (request.getQuantity() <= 0) {
                    cart.getItems().remove(item);
                    cartItemRepository.delete(item);
                } else if (request.getQuantity() != 1) {
                    throw new IllegalArgumentException("Số lượng thú cưng phải là 1.");
                } else {
                    item.setQuantity(1);
                    cartItemRepository.save(item);
                }
            } else {
                throw new IllegalStateException("Loại item không xác định trong giỏ hàng.");
            }
            cartRepository.save(cart);
        }
        return convert.toCartResponse(cart);
    }

    @Transactional
    public CartResponse getCart(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));
        Cart cart = getOrCreateCart(user);

        return convert.toCartResponse(cart);
    }

    private Cart getOrCreateCart(User user) {
        Optional<Cart> existingCartOpt = cartRepository.findByUser(user);
        if(existingCartOpt.isPresent()){
            return existingCartOpt.get();
        } else {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setItems(new ArrayList<>());
            return cartRepository.save(newCart);
        }
    }

    private void addPetToCart(Cart cart, Integer petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thú cưng với ID: " + petId));

        if (pet.getStatus() == null || !pet.getStatus()) {
            throw new IllegalArgumentException("Thú cưng '" + pet.getName() + "' hiện không có sẵn.");
        }

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> Global.ItemType.PET.equals(item.getItemType()) && item.getPet() != null && item.getPet().getId().equals(petId))
                .findFirst();

        if (existingItemOpt.isEmpty()) {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setPet(pet);
            newItem.setItemType(Global.ItemType.PET);
            newItem.setQuantity(1);
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }
    }

    private void addProductToCart(Cart cart, Integer productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Số lượng sản phẩm không hợp lệ.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + productId));

        if (product.getStockQuantity() == null || product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Sản phẩm '" + product.getName() + "' không đủ số lượng tồn kho. Chỉ còn " + (product.getStockQuantity() != null ? product.getStockQuantity() : 0) + " sản phẩm.");
        }

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> Global.ItemType.PRODUCT.equals(item.getItemType()) && item.getProduct() != null && item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + quantity;

            if (product.getStockQuantity() < newQuantity) {
                throw new IllegalArgumentException("Sản phẩm '" + product.getName() + "' không đủ số lượng tồn kho cho tổng số lượng yêu cầu (" + newQuantity + "). Chỉ còn " + product.getStockQuantity() + " sản phẩm.");
            }

            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setItemType(Global.ItemType.PRODUCT);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }
    }
}