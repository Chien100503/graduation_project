package com.petshop.petopia.service;

import com.petshop.petopia.dto.request.category.ProductFilterRequest;
import com.petshop.petopia.dto.response.product.BrandResponse;
import com.petshop.petopia.dto.response.product.ProductCategoryResponse;
import com.petshop.petopia.dto.response.product.ProductResponse;
import com.petshop.petopia.model.product.Product;
import com.petshop.petopia.model.product.ProductImage;
import com.petshop.petopia.repository.product.BrandRepository;
import com.petshop.petopia.repository.product.ProductCategoryRepository;
import com.petshop.petopia.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final BrandRepository brandRepository;

    @Transactional(readOnly = true)
    public List<ProductResponse> filterProducts(ProductFilterRequest filterRequest) {
        filterRequest.validate();

        Specification<Product> spec = Specification.where(null);

        if (filterRequest.getCategory() != null && !filterRequest.getCategory().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("prCategory").get("name"), filterRequest.getCategory()));
        }

        if (filterRequest.getBrand() != null && !filterRequest.getBrand().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("brand").get("name"), filterRequest.getBrand()));
        }

        if (filterRequest.getType() != null && !filterRequest.getType().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("type").get("name"), filterRequest.getType()));
        }

        return productRepository.findAll(spec).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductCategoryResponse> getAllProductCategories() {
        return productCategoryRepository.findAll().stream()
                .map(category -> new ProductCategoryResponse(category.getId(), category.getName()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BrandResponse> getAllProductBrands() {
        return brandRepository.findAll().stream()
                .map(brand -> new BrandResponse(brand.getId(), brand.getName()))
                .collect(Collectors.toList());
    }

    private ProductResponse mapToProductResponse(Product product) {
        List<String> imageUrls = product.getProductImages().stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getBrand() != null ? product.getBrand().getName() : null,
                product.getType() != null ? product.getType().getName() : null,
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getSize(),
                product.getWeight(),
                product.getExpirationDate(),
                imageUrls,
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getPrCategory() != null ? product.getPrCategory().getName() : null
        );
    }
}
