package com.petshop.petopia.service.admin;

import com.petshop.petopia.component.ConvertPet;
import com.petshop.petopia.dto.request.pet.CreatePetRequest;
import com.petshop.petopia.dto.response.admin.CreatePetResponse;
import com.petshop.petopia.model.pet.Breed;
import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.pet.PetCategory;
import com.petshop.petopia.model.pet.PetImage;
import com.petshop.petopia.repository.pet.BreedRepository;
import com.petshop.petopia.repository.pet.PetCategoryRepository;
import com.petshop.petopia.repository.pet.PetImageRepository;
import com.petshop.petopia.repository.pet.PetRepository;
import com.petshop.petopia.service.FirebaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminPetService {

    private final PetRepository petRepository;
    private final PetCategoryRepository petCategoryRepository;
    private final FirebaseService firebaseService;
    private final ConvertPet convertPet;
    private final BreedRepository breedRepository;
    private final PetImageRepository petImageRepository;

    @Transactional
    public CreatePetResponse createPet(CreatePetRequest req) throws IOException {
        Breed breed = null;
        PetCategory category;
        Optional<PetCategory> categoryOpt = petCategoryRepository.findByName(req.getPetCategoryName());
        if (categoryOpt.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy danh mục sản phẩm: " + req.getPetCategoryName());
        }
        category = categoryOpt.get();
        if (req.getBreedName() != null && !req.getBreedName().isEmpty()) {
            Optional<Breed> breedOpt = breedRepository.findByNameAndPetCategory(req.getBreedName(), category);
            if (breedOpt.isEmpty()) {
                throw new IllegalArgumentException("Không tìm thấy giống '" + req.getBreedName() + "' trong danh mục '" + req.getPetCategoryName() + "'.");
            }
            breed = breedOpt.get();
        }

        Pet pet = new Pet();
        pet.setName(req.getName());
        pet.setBreed(breed);
        pet.setAge(req.getAge());
        pet.setGender(req.getGender());
        pet.setSize(req.getSize());
        pet.setWeight(req.getWeight());
        pet.setColor(req.getColor());
        pet.setPrice(req.getPrice());
        pet.setStatus("Available".equalsIgnoreCase(req.getStatus()));
        pet.setDescription(req.getDescription());
        pet.setPetCategory(category);

        if (req.getThumbnail() != null && !req.getThumbnail().isEmpty()) {
            String thumbnailUrl = firebaseService.uploadImageThumnail(req.getThumbnail());
            pet.setThumnail(thumbnailUrl);
        }

        Pet savedPet = petRepository.save(pet);

        List<PetImage> petImages = new ArrayList<>();
        if (req.getFile() != null && !req.getFile().isEmpty()) {
            for (MultipartFile file : req.getFile()) {
                String imageUrl = firebaseService.uploadImagePet(file);
                PetImage petImage = new PetImage();
                petImage.setImageUrl(imageUrl);
                petImage.setPet(savedPet);
                petImages.add(petImage);
            }
            petImageRepository.saveAll(petImages);
            savedPet.setPetImages(petImages);
        }
        return convertPet.convertToResponse(savedPet);
    }


}
