package com.petshop.petopia.service;

import com.petshop.petopia.dto.response.product.*;
import com.petshop.petopia.model.product.*;
import com.petshop.petopia.repository.product.*;
import com.petshop.petopia.component.ConvertProduct;
import com.petshop.petopia.repository.user.WishlistRepository;
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
    private final WishlistRepository wishlistRepository;

    public List<GetAllProductResponse> getAllProducts(Integer userId) {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> {
                    boolean isInWishlist = wishlistRepository.existsByUserIdAndProductId(userId, product.getId());
                    return convertProduct.convertToGetAllProductResponse(product, isInWishlist);
                })
                .collect(Collectors.toList());
    }

    public GetProductDetailResponse getProductDetail(Integer userId, Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + productId));
        boolean isInWishlist = wishlistRepository.existsByUserIdAndProductId(userId, productId);
        return convertProduct.convertToGetProductDetailResponse(product, isInWishlist);
    }

    public Optional<List<TypeResponse>> getTypesByCategoryId(Integer categoryId) {
        return typeRepository.findByProductCategory_Id(categoryId)
                .map(types -> types.stream()
                        .map(type -> new TypeResponse(type.getId(), type.getName()))
                        .collect(Collectors.toList()));
    }

    public List<GetAllProductResponse> getProductsByCategoryAndType(Integer userId, Integer categoryId, Integer typeId) {
        List<Product> products = productRepository.findByPrCategory_IdAndType_Id(categoryId, typeId);
        return products.stream()
                .map(product -> {
                    boolean isInWishlist = wishlistRepository.existsByUserIdAndProductId(userId, product.getId());
                    return convertProduct.convertToGetAllProductResponse(product, isInWishlist);
                })
                .collect(Collectors.toList());
    }

    public List<GetAllProductResponse> getProductsByCategory(Integer userId, Integer categoryId) {
        List<Product> products = productRepository.findByPrCategory_Id(categoryId);
        return products.stream()
                .map(product -> {
                    boolean isInWishlist = wishlistRepository.existsByUserIdAndProductId(userId, product.getId());
                    return convertProduct.convertToGetAllProductResponse(product, isInWishlist);
                })
                .collect(Collectors.toList());
    }
}
