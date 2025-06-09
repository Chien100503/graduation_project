package com.petshop.petopia.service.user;

import com.petshop.petopia.component.ConvertPet;
import com.petshop.petopia.component.ConvertProduct;
import com.petshop.petopia.dto.response.pet.GetAllPetResponse;
import com.petshop.petopia.dto.response.product.GetAllProductResponse;
import com.petshop.petopia.dto.response.user.WishlistItemResponse;
import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.product.Product;
import com.petshop.petopia.model.user.User;
import com.petshop.petopia.model.user.WishList;
import com.petshop.petopia.repository.user.UserRepository;
import com.petshop.petopia.repository.user.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ConvertPet convertPet;
    private final ConvertProduct convertProduct;

    @Transactional
    public List<WishlistItemResponse> getUserWishList(Integer userId) {
        List<WishList> wishlists = wishlistRepository.findByUserId(userId);
        List<WishlistItemResponse> responses = new ArrayList<>();

        for (WishList item : wishlists) {
            if (item.getPet() != null) {
                Pet pet = item.getPet();
                if (pet.getStatus() != null && !pet.getStatus()) {
                    wishlistRepository.delete(item);
                    continue;
                }
                GetAllPetResponse petDto = convertPet.convertToGetAllPetResponse(pet, true);
                responses.add(new WishlistItemResponse("pet", petDto, null));

            } else if (item.getProduct() != null) {
                Product product = item.getProduct();
                GetAllProductResponse productDto = convertProduct.convertToGetAllProductResponse(product);
                responses.add(new WishlistItemResponse("product", null, productDto));
            }
        }

        return responses;
    }

    @Transactional
    public void addToWishlist(Integer userId, Integer petId, Integer productId) {
        if (petId == null && productId == null) {
            throw new IllegalArgumentException("petId hoặc productId phải khác null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user với id: " + userId));

        if (petId != null && !wishlistRepository.existsByUserIdAndPetId(userId, petId)) {
            Pet pet = new Pet();
            pet.setId(petId);
            WishList wishList = new WishList();
            wishList.setUser(user);
            wishList.setPet(pet);
            wishlistRepository.save(wishList);
        }

        if (productId != null && !wishlistRepository.existsByUserIdAndPetId(userId, productId)) {
            Product product = new Product();
            product.setId(productId);
            WishList wishList = new WishList();
            wishList.setUser(user);
            wishList.setProduct(product);
            wishlistRepository.save(wishList);
        }
    }

    @Transactional
    public void removeFromWishlist(Integer userId, Integer petId, Integer productId) {
        if (petId == null && productId == null) {
            throw new IllegalArgumentException("petId hoặc productId phải khác null");
        }

        if (petId != null && productId == null) {
            wishlistRepository.deleteByUserIdAndPetId(userId, petId);
        }

        if (productId != null && petId == null) {
            wishlistRepository.deleteByUserIdAndPetId(userId, productId);
        }
    }
}
