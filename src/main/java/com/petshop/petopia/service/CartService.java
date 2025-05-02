package com.petshop.petopia.service;

import com.petshop.petopia.dto.request.cart.CartItemRequest;
import com.petshop.petopia.dto.request.cart.CartItemUpdateRequest;
import com.petshop.petopia.dto.response.cart.CartResponse;
import com.petshop.petopia.model.cart.Cart;
import com.petshop.petopia.model.cart.CartItem;
import com.petshop.petopia.model.ItemType;
import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.product.Product;
import com.petshop.petopia.model.user.User;
import com.petshop.petopia.repository.cart.CartItemRepository;
import com.petshop.petopia.repository.cart.CartRepository;
import com.petshop.petopia.repository.pet.PetRepository;
import com.petshop.petopia.repository.product.ProductRepository;
import com.petshop.petopia.repository.user.UserRepository;
import com.petshop.petopia.util.ConvertCart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils; // Import CollectionUtils

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));
        Cart cart = getOrCreateCart(user);

        // Kiểm tra request null
        if (request == null) {
            throw new IllegalArgumentException("Yêu cầu thêm vào giỏ hàng không hợp lệ: Request không được null.");
        }

        // Sử dụng kiểm tra logic rõ ràng hơn cho request
        if (request.getPetId() != null && request.getProductId() == null) {
            addPetToCart(cart, request.getPetId());
        } else if (request.getProductId() != null && request.getPetId() == null) {
            addProductToCart(cart, request.getProductId(), request.getQuantity());
        } else {
            // Xử lý trường hợp cả hai ID đều null hoặc cả hai đều không null
            throw new IllegalArgumentException("Yêu cầu thêm vào giỏ hàng không hợp lệ. Chỉ cung cấp PetId hoặc ProductId.");
        }

        recalculateCartTotalPrice(cart);
        return convert.toCartResponse(cart);
    }

    @Transactional
    public CartResponse updateCartItem(Integer userId, CartItemUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));
        Cart cart = getOrCreateCart(user);

        // Tìm item và đảm bảo nó thuộc về giỏ hàng của người dùng này
        CartItem item = cartItemRepository.findByIdAndCart(request.getItemId(), cart)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy item trong giỏ hàng của người dùng với ID: " + request.getItemId()));

        // Kiểm tra request null
        if (request == null) {
            throw new IllegalArgumentException("Yêu cầu cập nhật giỏ hàng không hợp lệ: Request không được null.");
        }

        if (request.getQuantity() != null) {
            // **Sử dụng Enum để kiểm tra loại item**
            if (item.getItemType() == ItemType.PRODUCT) {
                // Là sản phẩm
                Product product = item.getProduct();
                if (request.getQuantity() <= 0) {
                    cartItemRepository.delete(item);
                } else if (product.getStockQuantity() < request.getQuantity()) {
                    throw new IllegalArgumentException("Không đủ số lượng sản phẩm trong kho.");
                } else {
                    item.setQuantity(request.getQuantity());
                    item.setItemTotalPrice(item.getPrice() * request.getQuantity());
                    cartItemRepository.save(item);
                }
            } else if (item.getItemType() == ItemType.PET) {
                // Là thú cưng
                if (request.getQuantity() != 1) {
                    throw new IllegalArgumentException("Số lượng thú cưng phải là 1.");
                }
                item.setQuantity(1);
                item.setItemTotalPrice(item.getPrice());
                cartItemRepository.save(item);
            } else {
                // Trường hợp này không nên xảy ra nếu item_type luôn được thiết lập đúng khi tạo
                throw new IllegalStateException("Loại item không xác định trong giỏ hàng.");
            }
            recalculateCartTotalPrice(cart);
        }
        return convert.toCartResponse(cart);
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));
        Cart cart = getOrCreateCart(user);
        recalculateCartTotalPrice(cart);
        return convert.toCartResponse(cart);
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setTotalPrice(0);
                    return cartRepository.save(newCart);
                });
    }

    private void addPetToCart(Cart cart, Integer petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thú cưng với ID: " + petId));

        // Kiểm tra xem pet này đã có trong giỏ hàng chưa
        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartAndPet(cart, pet);

        if (existingItemOpt.isEmpty()) {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setPet(pet);
            newItem.setItemType(ItemType.PET);
            newItem.setQuantity(1);
            newItem.setPrice(pet.getPrice());
            newItem.setItemTotalPrice(pet.getPrice());
            cartItemRepository.save(newItem);
        }
        //  Nếu đã tồn tại, không làm gì cả
    }

    private void addProductToCart(Cart cart, Integer productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Số lượng sản phẩm không hợp lệ.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + productId));

        // Kiểm tra xem sản phẩm này đã có trong giỏ hàng chưa
        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItemOpt.isPresent()) {
            // Sản phẩm đã có trong giỏ, tăng số lượng
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setItemTotalPrice(existingItem.getPrice() * existingItem.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            // Sản phẩm chưa có, tạo CartItem mới
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setItemType(ItemType.PRODUCT);
            newItem.setQuantity(quantity);
            newItem.setPrice(product.getPrice());
            newItem.setItemTotalPrice(product.getPrice() * quantity);
            cartItemRepository.save(newItem);
        }
    }

    private void recalculateCartTotalPrice(Cart cart) {
        Integer totalPrice = 0;
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        if (!CollectionUtils.isEmpty(cartItems)) {
            for (CartItem item : cartItems) {
                totalPrice += (item.getItemTotalPrice() != null ? item.getItemTotalPrice() : 0);
            }
        }
        cart.setTotalPrice(totalPrice);
        cart.setItems(cartItems);
        cartRepository.save(cart);
    }
}