// 📄 service/PetService.java
package com.petshop.petopia.service;

import com.petshop.petopia.dto.request.admin.PetCreateRequest;
import com.petshop.petopia.dto.response.PetResponse;
import com.petshop.petopia.model.product.Pet;
import com.petshop.petopia.model.product.ProductCategory;
import com.petshop.petopia.repository.product.PetRepository;
import com.petshop.petopia.repository.product.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final ProductCategoryRepository categoryRepository;
    private final FirebaseService firebaseService;

    public PetResponse createPet(PetCreateRequest req) throws IOException {
        // 🖼 Upload ảnh
        String imageUrl = firebaseService.uploadImagePet(req.getFile());

        Pet pet = new Pet();
        pet.setName(req.getName());
        pet.setBreed(req.getBreed());
        pet.setAge(req.getAge());
        pet.setGender(req.getGender());
        pet.setSize(req.getSize());
        pet.setWeight(req.getWeight());
        pet.setColor(req.getColor());
        pet.setPrice(req.getPrice());
        pet.setStatus("Available".equalsIgnoreCase(req.getStatus()));
        pet.setHealthStatus(req.getHealthStatus());
        pet.setDescription(req.getDescription());
        pet.setImg(imageUrl);

        // 🔎 Tìm hoặc tạo category
        Optional<ProductCategory> categoryOpt = categoryRepository.findByName(req.getProductCategoryName());
        ProductCategory category = categoryOpt.orElseGet(() -> {
            ProductCategory newCategory = new ProductCategory();
            newCategory.setName(req.getProductCategoryName());
            newCategory.setDescription("No description available");
            newCategory.setCreatedAt(new Date());
            return categoryRepository.save(newCategory);
        });
        pet.setPrCategory(category);

        // 💾 Lưu pet vào DB
        Pet savedPet = petRepository.save(pet);

        // 🔁 Trả về dạng response
        return convertToResponse(savedPet);
    }

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    private PetResponse convertToResponse(Pet pet) {
        return new PetResponse(
                pet.getPid(),
                pet.getName(),
                pet.getBreed(),
                pet.getAge(),
                pet.getGender(),
                pet.getSize(),
                pet.getWeight(),
                pet.getColor(),
                pet.getPrice(),
                pet.getStatus() != null && pet.getStatus() ? "Available" : "Ordered",
                pet.getHealthStatus(),
                pet.getDescription(),
                pet.getImg(),
                pet.getCreatedAt(),
                pet.getUpdatedAt(),
                pet.getPrCategory() != null ? pet.getPrCategory().getName() : null
        );
    }
}
