package com.petshop.petopia.service;

import com.petshop.petopia.dto.response.pet.GetAllPetResponse;
import com.petshop.petopia.dto.response.pet.PetDetailResponse;
import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.repository.pet.PetRepository;
import com.petshop.petopia.component.ConvertPet;
import com.petshop.petopia.repository.user.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService {
    private final PetRepository petRepository;
    private final ConvertPet convertPet;
    private final WishlistRepository wishlistRepository;

    @Transactional
    public PetDetailResponse getPetById(Integer userId, Integer petId) {
        Pet pet = petRepository.findByIdAndStatusTrue(petId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thú cưng với ID: " + petId));

        boolean isInWishlist = wishlistRepository.existsByUserIdAndPetId(userId, petId);
        return convertPet.convertToGetPetDetailResponse(pet, isInWishlist);
    }

    @Transactional
    public List<GetAllPetResponse> getAllPets(Integer userId) {
        List<Pet> pets = petRepository.findByStatusTrue();
        return pets.stream()
                .map(pet -> {
                    boolean isInWishlist = wishlistRepository.existsByUserIdAndPetId(userId, pet.getId());
                    return convertPet.convertToGetAllPetResponse(pet, isInWishlist);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<GetAllPetResponse> getPetByBreed(Integer userId, Integer categoryId, Integer breedId) {
        List<Pet> pets = petRepository.findByPetCategory_IdAndBreed_IdAndStatusTrue(categoryId, breedId);
        return pets.stream()
                .map(pet -> {
                    boolean isInWishlist = wishlistRepository.existsByUserIdAndPetId(userId, pet.getId());
                    return convertPet.convertToGetAllPetResponse(pet, isInWishlist);
                })
                .collect(Collectors.toList());
    }
}
