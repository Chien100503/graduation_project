package com.petshop.petopia.service;

import com.petshop.petopia.dto.request.admin.PetCreateRequest;
import com.petshop.petopia.dto.response.PetCreateResponse;
import com.petshop.petopia.dto.response.pet.GetPetResponse;
import com.petshop.petopia.model.pet.Breed;
import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.pet.PetCategory;
import com.petshop.petopia.model.pet.PetImage;
import com.petshop.petopia.repository.pet.BreedRepository;
import com.petshop.petopia.repository.pet.PetCategoryRepository;
import com.petshop.petopia.repository.pet.PetImageRepository;
import com.petshop.petopia.repository.pet.PetRepository;
import com.petshop.petopia.util.ConvertPet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final PetCategoryRepository petCategoryRepository;
    private final FirebaseService firebaseService;
    private final ConvertPet convertPet;
    private final BreedRepository breedRepository;
    private final PetImageRepository petImageRepository;

    @Transactional
    public PetCreateResponse createPet(PetCreateRequest req) throws IOException {
        // 1. Tìm hoặc tạo Breed
        Breed breed;
        if (req.getBreedName() != null && !req.getBreedName().isEmpty()) {
            Optional<Breed> breedOpt = breedRepository.findByName(req.getBreedName());
            breed = breedOpt.orElseGet(() -> {
                Breed newBreed = new Breed();
                newBreed.setName(req.getBreedName());
                newBreed.setCreatedAt(new Date());
                return breedRepository.save(newBreed);
            });
        } else {
            breed = null;
        }

        // 2. Tìm hoặc tạo PetCategory
        Optional<PetCategory> categoryOpt = petCategoryRepository.findByName(req.getPetCategoryName());
        PetCategory category = categoryOpt.orElseGet(() -> {
            PetCategory newCategory = new PetCategory();
            newCategory.setName(req.getPetCategoryName());
            newCategory.setDescription("No description available");
            newCategory.setCreatedAt(new Date());
            return petCategoryRepository.save(newCategory);
        });

        // 3. Tạo Pet object
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


        // 4. Lưu Pet (để có ID cho PetImage)
        Pet savedPet = petRepository.save(pet);


        // 5. Xử lý hình ảnh: Tải lên, tạo PetImage objects, và lưu
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


        // 6. Trả về response
        return convertPet.convertToResponse(savedPet);
    }

    @Transactional(readOnly = true)
    public GetPetResponse getPetById(Integer petId) {
        // Tìm Pet entity theo ID
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thú cưng với ID: " + petId));

        GetPetResponse petDto = convertPet.convertToGetPetResponse(pet);

        return petDto;
    }

    @Transactional(readOnly = true)
    public List<GetPetResponse> getAllPets() {
        List<Pet> pets = petRepository.findAll();

        // Chuyển đổi danh sách Pet entities sang danh sách PetResponseDto
        return pets.stream()
                .map(convertPet::convertToGetPetResponse) // Sử dụng phương thức chuyển đổi
                .collect(Collectors.toList());
    }
}