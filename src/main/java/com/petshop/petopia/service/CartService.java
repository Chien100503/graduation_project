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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// Import CollectionUtils

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository; // Có thể không cần nếu dùng cascade REMOVE và ORPHAN REMOVAL
    private final ProductRepository productRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final ConvertCart convert;

    @Transactional
    public CartResponse addToCart(Integer userId, CartItemRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));
        Cart cart = getOrCreateCart(user);

        if (request == null) {
            throw new IllegalArgumentException("Yêu cầu thêm vào giỏ hàng không hợp lệ: Request không được null.");}

        if (request.getPetId() != null && request.getProductId() == null) {
            addPetToCart(cart, request.getPetId());
        } else if (request.getProductId() != null && request.getPetId() == null) {
            addProductToCart(cart, request.getProductId(), request.getQuantity());
        } else {
            throw new IllegalArgumentException("Yêu cầu thêm vào giỏ hàng không hợp lệ. Chỉ cung cấp PetId hoặc ProductId.");
        }

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
            Integer oldItemTotalPrice = item.getItemTotalPrice();
            if (item.getItemType() == ItemType.PRODUCT) {
                Product product = item.getProduct();
                if (request.getQuantity() <= 0) {
                    cart.getItems().remove(item);
                    cart.setTotalPrice(cart.getTotalPrice() - oldItemTotalPrice);

                } else if (product.getStockQuantity() < request.getQuantity()) {
                    throw new IllegalArgumentException("Không đủ số lượng sản phẩm trong kho.");
                } else {
                    item.setQuantity(request.getQuantity());
                    int newItemTotalPrice = item.getPrice() * request.getQuantity();
                    item.setItemTotalPrice(newItemTotalPrice);
                    cart.setTotalPrice(cart.getTotalPrice() - oldItemTotalPrice + newItemTotalPrice);
                }
            } else if (item.getItemType() == ItemType.PET) {
                // Là thú cưng - số lượng chỉ có thể là 1 hoặc 0 (để xóa)
                if (request.getQuantity() <= 0) {
                    cart.getItems().remove(item);
                    cart.setTotalPrice(cart.getTotalPrice() - oldItemTotalPrice);

                } else if (request.getQuantity() != 1) {
                    throw new IllegalArgumentException("Số lượng thú cưng phải là 1.");
                } else {
                    item.setQuantity(1);
                    Integer newItemTotalPrice = item.getPrice();
                    item.setItemTotalPrice(newItemTotalPrice);
                    if (!oldItemTotalPrice.equals(newItemTotalPrice)) {
                        cart.setTotalPrice(cart.getTotalPrice() - oldItemTotalPrice + newItemTotalPrice);
                    }
                }
            } else {
                throw new IllegalStateException("Loại item không xác định trong giỏ hàng.");
            }
            cartRepository.save(cart);
        }

        return convert.toCartResponse(cart);
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));
        Cart cart = getOrCreateCart(user);
        cart.getItems().size();

        return convert.toCartResponse(cart);
    }

    private Cart getOrCreateCart(User user) {
        Optional<Cart> existingCartOpt = cartRepository.findByUser(user);
        if(existingCartOpt.isPresent()){
            return existingCartOpt.get();
        } else {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setTotalPrice(0);
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
                .filter(item -> ItemType.PET.equals(item.getItemType()) && item.getPet() != null && item.getPet().getId().equals(petId))
                .findFirst();

        if (existingItemOpt.isEmpty()) {
            CartItem newItem = new CartItem();
            newItem.setCart(cart); // Set mối quan hệ ngược
            newItem.setPet(pet);
            newItem.setItemType(ItemType.PET);
            newItem.setQuantity(1);
            newItem.setPrice(pet.getPrice());
            newItem.setItemTotalPrice(pet.getPrice());
            cart.getItems().add(newItem);
            cart.setTotalPrice(cart.getTotalPrice() + newItem.getItemTotalPrice());
            cartRepository.save(cart);
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
                .filter(item -> ItemType.PRODUCT.equals(item.getItemType()) && item.getProduct() != null && item.getProduct().getId().equals(productId))
                .findFirst();


        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();

            Integer oldItemTotalPrice = existingItem.getItemTotalPrice();
            if (oldItemTotalPrice == null) oldItemTotalPrice = 0; // Xử lý trường hợp null

            int newQuantity = existingItem.getQuantity() + quantity;

            if (product.getStockQuantity() < newQuantity) {
                throw new IllegalArgumentException("Sản phẩm '" + product.getName() + "' không đủ số lượng tồn kho cho tổng số lượng yêu cầu (" + newQuantity + "). Chỉ còn " + product.getStockQuantity() + " sản phẩm.");
            }

            existingItem.setQuantity(newQuantity); // Cập nhật số lượng
            int newItemTotalPrice = existingItem.getPrice() * newQuantity; // Tính lại tổng giá item
            existingItem.setItemTotalPrice(newItemTotalPrice);

            cart.setTotalPrice(cart.getTotalPrice() - oldItemTotalPrice + newItemTotalPrice);
            cartRepository.save(cart); // Lưu cart sau khi cập nhật total price
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setItemType(ItemType.PRODUCT);
            newItem.setQuantity(quantity);
            newItem.setPrice(product.getPrice());
            Integer newItemTotalPrice = product.getPrice() * quantity;
            newItem.setItemTotalPrice(newItemTotalPrice);

            cart.getItems().add(newItem);
            cart.setTotalPrice(cart.getTotalPrice() + newItemTotalPrice);

            cartRepository.save(cart); // Lưu cart sau khi thay đổi
        }
    }
}