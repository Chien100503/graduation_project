package com.petshop.petopia.service;

import com.petshop.petopia.dto.response.product.*;
import com.petshop.petopia.model.product.*;
import com.petshop.petopia.repository.product.*;
import com.petshop.petopia.component.ConvertProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ConvertProduct convertProduct;
    private final TypeRepository typeRepository;

    public List<GetAllProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(convertProduct::convertToGetProductResponse)
                .collect(Collectors.toList());
    }

    public Optional<GetProductDetailResponse> getProductById(Integer id) {
        return productRepository.findById(id)
                .map(convertProduct::convertToGetProductDetailResponse);
    }

    public Optional<List<TypeResponse>> getTypesByCategoryId(Integer categoryId) {
        return typeRepository.findByProductCategory_Id(categoryId)
                .map(types -> types.stream()
                        .map(type -> new TypeResponse(type.getId(), type.getName()))
                        .collect(Collectors.toList()));
    }

    public List<ProductCategoryResponse> getProductsByCategoryAndType(Integer categoryId, Integer typeId) {
        List<Product> products = productRepository.findByPrCategory_IdAndType_Id(categoryId, typeId);
        return products.stream()
                .map(convertProduct::convertToProductCategoryResponse)
                .collect(Collectors.toList());
    }
}

