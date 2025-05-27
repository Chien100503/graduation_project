package com.petshop.petopia.service.category;

import com.petshop.petopia.component.Global;
import com.petshop.petopia.dto.response.user.CategoryResponse;
import com.petshop.petopia.repository.pet.PetCategoryRepository;
import com.petshop.petopia.repository.product.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final PetCategoryRepository petCategoryRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        List<CategoryResponse> petCategoriesResponse = petCategoryRepository.findAll().stream()
                .map(petCategory -> new CategoryResponse(
                        petCategory.getId(),
                        petCategory.getName(),
                        Global.ItemType.PET,
                        petCategory.getImageUrl()
                ))
                .toList();
        List<CategoryResponse> allCategories = new ArrayList<>(petCategoriesResponse);

        List<CategoryResponse> productCategoriesResponse = productCategoryRepository.findAll().stream()
                .map(productCategory -> new CategoryResponse(
                        productCategory.getId(),
                        productCategory.getName(),
                        Global.ItemType.PRODUCT,
                        productCategory.getImageUrl()
                ))
                .toList();
        allCategories.addAll(productCategoriesResponse);

        return allCategories;
    }
}
