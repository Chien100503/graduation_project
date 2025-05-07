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

import java.util.ArrayList; // Vẫn cần nếu dùng new ArrayList trong getOrCreateCart
import java.util.List;
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
        // Đảm bảo cart được load/tạo trong cùng transaction
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

        // Loại bỏ dòng gọi recalculateCartTotalPrice(cart); ở đây
        // cartRepository.save(cart); // Có thể cần save cart ở cuối transaction nếu không dùng cascade đầy đủ

        return convert.toCartResponse(cart);
    }

    @Transactional
    public CartResponse updateCartItem(Integer userId, CartItemUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));
        // Đảm bảo cart được load trong cùng transaction
        Cart cart = getOrCreateCart(user); // Lấy giỏ hàng để cập nhật total price

        // Tìm item và đảm bảo nó thuộc về giỏ hàng của người dùng này
        // Lấy item trực tiếp từ collection của cart nếu có thể, hoặc query
        Optional<CartItem> itemOpt = cart.getItems().stream()
                .filter(item -> item.getId().equals(request.getItemId()))
                .findFirst();

        // Nếu không tìm thấy trong collection của entity managed, query từ repo (fallback)
        CartItem item = itemOpt.orElseGet(() -> cartItemRepository.findByIdAndCart(request.getItemId(), cart)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy item trong giỏ hàng của người dùng với ID: " + request.getItemId())));


        // Kiểm tra request null
        if (request == null) {
            throw new IllegalArgumentException("Yêu cầu cập nhật giỏ hàng không hợp lệ: Request không được null.");
        }

        if (request.getQuantity() != null) {
            // Lưu lại tổng giá cũ của item trước khi thay đổi số lượng
            Integer oldItemTotalPrice = item.getItemTotalPrice();

            // **Sử dụng Enum để kiểm tra loại item**
            if (item.getItemType() == ItemType.PRODUCT) {
                // Là sản phẩm
                Product product = item.getProduct();
                if (request.getQuantity() <= 0) {
                    // Xóa item
                    cart.getItems().remove(item); // Xóa khỏi collection để kích hoạt orphan removal
                    // cartItemRepository.delete(item); // Có thể không cần nếu dùng orphanRemoval=true

                    // Cập nhật tổng giá giỏ hàng: trừ đi giá của item bị xóa
                    cart.setTotalPrice(cart.getTotalPrice() - oldItemTotalPrice);

                } else if (product.getStockQuantity() < request.getQuantity()) {
                    throw new IllegalArgumentException("Không đủ số lượng sản phẩm trong kho.");
                } else {
                    // Cập nhật số lượng
                    item.setQuantity(request.getQuantity());
                    Integer newItemTotalPrice = item.getPrice() * request.getQuantity();
                    item.setItemTotalPrice(newItemTotalPrice);
                    // cartItemRepository.save(item); // Có thể không cần nếu dùng cascade MERGE

                    // Cập nhật tổng giá giỏ hàng: cộng thêm chênh lệch giá
                    cart.setTotalPrice(cart.getTotalPrice() - oldItemTotalPrice + newItemTotalPrice);
                }
            } else if (item.getItemType() == ItemType.PET) {
                // Là thú cưng - số lượng chỉ có thể là 1 hoặc 0 (để xóa)
                if (request.getQuantity() <= 0) {
                    // Xóa thú cưng
                    cart.getItems().remove(item); // Xóa khỏi collection để kích hoạt orphan removal
                    // cartItemRepository.delete(item); // Có thể không cần

                    // Cập nhật tổng giá giỏ hàng: trừ đi giá của thú cưng bị xóa
                    cart.setTotalPrice(cart.getTotalPrice() - oldItemTotalPrice);

                } else if (request.getQuantity() != 1) {
                    throw new IllegalArgumentException("Số lượng thú cưng phải là 1.");
                } else {
                    // Số lượng vẫn là 1, không cần làm gì nhiều, đảm bảo giá đúng
                    item.setQuantity(1);
                    Integer newItemTotalPrice = item.getPrice();
                    item.setItemTotalPrice(newItemTotalPrice); // Đảm bảo giá đúng
                    // cartItemRepository.save(item); // Có thể không cần

                    // Cập nhật tổng giá giỏ hàng: chỉ cập nhật nếu giá thay đổi (ví dụ giá pet thay đổi)
                    if (!oldItemTotalPrice.equals(newItemTotalPrice)) {
                        cart.setTotalPrice(cart.getTotalPrice() - oldItemTotalPrice + newItemTotalPrice);
                    }
                }
            } else {
                throw new IllegalStateException("Loại item không xác định trong giỏ hàng.");
            }
            // Lưu lại đối tượng cart đã được cập nhật tổng giá
            cartRepository.save(cart); // <<< Lưu cart sau khi cập nhật tổng giá và collection
        }
        // Loại bỏ dòng gọi recalculateCartTotalPrice(cart); ở đây

        return convert.toCartResponse(cart);
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));
        // Vẫn cần ensure cart exists, nhưng không cần recalculate total price nếu logic thêm/update đã đúng
        Cart cart = getOrCreateCart(user);
        // Nếu bạn tin tưởng vào logic incremental update, không cần dòng này
        // Tuy nhiên, để an toàn, bạn CÓ THỂ chạy recalculate tính lại từ đầu ở đây
        // chỉ khi lấy giỏ hàng ra hiển thị, để đảm bảo dữ liệu luôn đúng
        // nhưng hãy dùng phương thức tính toán trực tiếp từ items trong bộ nhớ
        // hoặc query 1 lần count/sum nếu cần.
        // Phương án tốt nhất: logic incremental update *phải* đúng, và bạn chỉ cần get cart.
        // recalculateCartTotalPrice(cart); // Bỏ dòng này

        // Lấy items đảm bảo hibernate load collection nếu nó lazy
        cart.getItems().size(); // Force collection initialization if lazy

        return convert.toCartResponse(cart);
    }

    private Cart getOrCreateCart(User user) {
        // Cần đảm bảo khi tạo Cart mới, collection items được khởi tạo (ví dụ: new ArrayList<>() )
        Optional<Cart> existingCartOpt = cartRepository.findByUser(user);
        if(existingCartOpt.isPresent()){
            return existingCartOpt.get();
        } else {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setTotalPrice(0);
            newCart.setItems(new ArrayList<>()); // <-- Khởi tạo collection khi tạo mới
            return cartRepository.save(newCart);
        }
    }

    private void addPetToCart(Cart cart, Integer petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thú cưng với ID: " + petId));

        // Kiểm tra xem pet này đã có trong giỏ hàng chưa
        // Tìm trong collection đang được managed
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> ItemType.PET.equals(item.getItemType()) && item.getPet() != null && item.getPet().getId().equals(petId))
                .findFirst();


        if (existingItemOpt.isEmpty()) {
            CartItem newItem = new CartItem();
            newItem.setCart(cart); // Set mối quan hệ ngược
            newItem.setPet(pet);
            newItem.setItemType(ItemType.PET);
            newItem.setQuantity(1);
            newItem.setPrice(pet.getPrice()); // Lưu giá pet tại thời điểm thêm
            newItem.setItemTotalPrice(pet.getPrice()); // Tổng giá item = giá * số lượng (1)

            // --- THAO TÁC CHUẨN & CẬP NHẬT TỔNG GIÁ ---
            cart.getItems().add(newItem); // <<< Thêm vào collection của cart
            cart.setTotalPrice(cart.getTotalPrice() + newItem.getItemTotalPrice()); // <<< Cập nhật tổng giá

            // cartItemRepository.save(newItem); // Có thể bỏ nếu có cascade PERSIST trên Cart.items
            cartRepository.save(cart); // <<< Lưu cart sau khi thay đổi collection và tổng giá
        }
        //  Nếu đã tồn tại (pet chỉ có số lượng 1), không làm gì cả, tổng giá không đổi
    }

    private void addProductToCart(Cart cart, Integer productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Số lượng sản phẩm không hợp lệ.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + productId));

        // Kiểm tra xem sản phẩm này đã có trong giỏ hàng chưa
        // Tìm trong collection đang được managed
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> ItemType.PRODUCT.equals(item.getItemType()) && item.getProduct() != null && item.getProduct().getId().equals(productId))
                .findFirst();


        if (existingItemOpt.isPresent()) {
            // Sản phẩm đã có trong giỏ, tăng số lượng và cập nhật tổng giá
            CartItem existingItem = existingItemOpt.get();

            // Lưu lại tổng giá cũ của item trước khi thay đổi số lượng
            Integer oldItemTotalPrice = existingItem.getItemTotalPrice();

            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            Integer newItemTotalPrice = existingItem.getPrice() * existingItem.getQuantity();
            existingItem.setItemTotalPrice(newItemTotalPrice);

            // cartItemRepository.save(existingItem); // Có thể bỏ nếu có cascade MERGE trên Cart.items

            // Cập nhật tổng giá giỏ hàng: cộng thêm chênh lệch giá
            cart.setTotalPrice(cart.getTotalPrice() - oldItemTotalPrice + newItemTotalPrice);
            cartRepository.save(cart); // <<< Lưu cart sau khi cập nhật total price
        } else {
            // Sản phẩm chưa có, tạo CartItem mới và cập nhật tổng giá
            CartItem newItem = new CartItem();
            newItem.setCart(cart); // Set mối quan hệ ngược
            newItem.setProduct(product);
            newItem.setItemType(ItemType.PRODUCT);
            newItem.setQuantity(quantity);
            newItem.setPrice(product.getPrice()); // Lưu giá sản phẩm tại thời điểm thêm
            newItem.setItemTotalPrice(product.getPrice() * quantity);

            cart.getItems().add(newItem);
            cart.setTotalPrice(cart.getTotalPrice() + newItem.getItemTotalPrice());
            cartRepository.save(cart);
        }
    }
}