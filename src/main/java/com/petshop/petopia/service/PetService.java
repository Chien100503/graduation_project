package com.petshop.petopia.service;

import com.petshop.petopia.dto.response.pet.GetAllPetResponse;
import com.petshop.petopia.dto.response.pet.PetDetailResponse;
import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.repository.pet.PetRepository;
import com.petshop.petopia.component.ConvertPet;
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

    @Transactional
    public PetDetailResponse getPetById(Integer petId) {
        Pet pet = petRepository.findByIdAndStatusTrue(petId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thú cưng với ID: " + petId));

        return convertPet.convertToGetPetDetailResponse(pet);
    }

    @Transactional
    public List<GetAllPetResponse> getAllPets() {
        List<Pet> pets = petRepository.findByStatusTrue();
        return pets.stream()
                .map(convertPet::convertToGetAllPetResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<GetAllPetResponse> getPetByBreed(Integer categoryId, Integer breedId) {
        List<Pet> pets = petRepository.findByPetCategory_IdAndBreed_IdAndStatusTrue(categoryId, breedId);
        return pets.stream()
                .map(convertPet::convertToGetAllPetResponse)
                .collect(Collectors.toList());
    }
}