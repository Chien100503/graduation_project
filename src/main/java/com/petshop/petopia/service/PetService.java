// 📄 service/PetService.java
package com.petshop.petopia.service;

import com.petshop.petopia.dto.request.admin.PetCreateRequest;
import com.petshop.petopia.dto.response.PetResponse;
import com.petshop.petopia.model.product.Pet;
import com.petshop.petopia.model.product.PetCategory;
import com.petshop.petopia.repository.product.PetCategoryRepository;
import com.petshop.petopia.repository.product.PetRepository;
import com.petshop.petopia.util.ConvertPet;
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
    private final PetCategoryRepository petCategoryRepository;
    private final FirebaseService firebaseService;
    private final ConvertPet convertPet;

    public PetResponse createPet(PetCreateRequest req) throws IOException {
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
        Optional<PetCategory> categoryOpt = petCategoryRepository.findByName(req.getPetCategoryName());
        PetCategory category = categoryOpt.orElseGet(() -> {
            PetCategory newCategory = new PetCategory();
            newCategory.setName(req.getPetCategoryName());
            newCategory.setDescription("No description available");
            newCategory.setCreatedAt(new Date());
            return petCategoryRepository.save(newCategory);
        });
        pet.setPetCategory(category);

        // 💾 Lưu pet vào DB
        Pet savedPet = petRepository.save(pet);

        // 🔁 Trả về dạng response
        return convertPet.convertToResponse(savedPet);
    }

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }


}
