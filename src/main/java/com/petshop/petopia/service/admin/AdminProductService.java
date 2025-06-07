package com.petshop.petopia.service.admin;

import com.petshop.petopia.dto.request.product.CreateProductRequest;
import com.petshop.petopia.dto.response.product.CreateProductResponse;
import com.petshop.petopia.model.product.*;
import com.petshop.petopia.repository.product.*;
import com.petshop.petopia.component.ConvertProduct;
import com.petshop.petopia.service.FirebaseService;
import jakarta.persistence.EntityNotFoundException;
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
public class AdminProductService {

    private final FirebaseService firebaseService;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ConvertProduct convertProduct;
    private final BrandRepository brandRepository;
    private final TypeRepository typeRepository;
    private final ProductImageRepository productImageRepository;

    @Transactional
    public CreateProductResponse createProduct(CreateProductRequest req) throws IOException {
        Brand brand = brandRepository.findByName(req.getBrandName())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thương hiệu: " + req.getBrandName()));

        ProductCategory category = productCategoryRepository.findByName(req.getProductCategoryName())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục sản phẩm: " + req.getProductCategoryName()));

        Type type = typeRepository.findByNameAndProductCategory(req.getTypeName(), category)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy loại '" + req.getTypeName() + "' trong danh mục '" + req.getProductCategoryName() + "'."));

        Product product = new Product();
        product.setName(req.getName());
        product.setBrand(brand);
        product.setDescription(req.getDescription());
        product.setType(type);
        product.setSize(req.getSize());
        product.setWeight(req.getWeight());
        product.setPrice(req.getPrice());
        product.setStockQuantity(req.getStockQuantity());
        product.setExpirationDate(req.getExpirationDate());
        product.setPrCategory(category);

        if (req.getThumbnail() != null && !req.getThumbnail().isEmpty()) {
            String thumbnailUrl = firebaseService.uploadImageThumbnail(req.getThumbnail());
            product.setThumbnail(thumbnailUrl);
        }

        Product savedProduct = productRepository.save(product);

        List<ProductImage> productImages = new ArrayList<>();
        if (req.getFile() != null && !req.getFile().isEmpty()) {
            for (MultipartFile file : req.getFile()) {
                String imageUrl = firebaseService.uploadImageProduct(file);
                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(imageUrl);
                productImage.setProduct(savedProduct);
                productImages.add(productImage);
            }
            productImageRepository.saveAll(productImages);
            savedProduct.setProductImages(productImages);
        }

        return convertProduct.convertToCreateAndUpdateProductResponse(savedProduct);
    }

    @Transactional
    public CreateProductResponse updateProduct(Integer id, CreateProductRequest req) throws IOException {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với ID: " + id));

        Optional.ofNullable(req.getName()).ifPresent(existingProduct::setName);
        Optional.ofNullable(req.getDescription()).ifPresent(existingProduct::setDescription);
        Optional.ofNullable(req.getSize()).ifPresent(existingProduct::setSize);
        Optional.ofNullable(req.getWeight()).ifPresent(existingProduct::setWeight);
        Optional.ofNullable(req.getPrice()).ifPresent(existingProduct::setPrice);
        Optional.ofNullable(req.getStockQuantity()).ifPresent(existingProduct::setStockQuantity);
        Optional.ofNullable(req.getExpirationDate()).ifPresent(existingProduct::setExpirationDate);

        if (req.getBrandName() != null) {
            Brand brand = brandRepository.findByName(req.getBrandName())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thương hiệu: " + req.getBrandName()));
            existingProduct.setBrand(brand);
        }

        if (req.getProductCategoryName() != null) {
            ProductCategory category = productCategoryRepository.findByName(req.getProductCategoryName())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục sản phẩm: " + req.getProductCategoryName()));
            existingProduct.setPrCategory(category);

            if (req.getTypeName() != null) {
                Type type = typeRepository.findByNameAndProductCategory(req.getTypeName(), category)
                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy loại '" + req.getTypeName() + "' trong danh mục '" + req.getProductCategoryName() + "'."));
                existingProduct.setType(type);
            }
        } else if (req.getTypeName() != null) {
            ProductCategory currentCategory = existingProduct.getPrCategory();
            if (currentCategory == null) {
                throw new IllegalArgumentException("Không thể cập nhật loại sản phẩm khi danh mục sản phẩm hiện tại không xác định.");
            }
            Type type = typeRepository.findByNameAndProductCategory(req.getTypeName(), currentCategory)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy loại '" + req.getTypeName() + "' trong danh mục '" + currentCategory.getName() + "'."));
            existingProduct.setType(type);
        }
        if (req.getThumbnail() != null && !req.getThumbnail().isEmpty()) {
            // Nếu có thumbnail cũ, xóa nó khỏi Firebase
            if (existingProduct.getThumbnail() != null && !existingProduct.getThumbnail().isEmpty()) {
                firebaseService.deleteFileByUrl(existingProduct.getThumbnail());
            }
            String newThumbnailUrl = firebaseService.uploadImageThumbnail(req.getThumbnail());
            existingProduct.setThumbnail(newThumbnailUrl);
        }
        if (req.getFile() != null && !req.getFile().isEmpty()) {
            List<ProductImage> newProductImages = new ArrayList<>();
            for (MultipartFile file : req.getFile()) {
                String imageUrl = firebaseService.uploadImageProduct(file);
                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(imageUrl);
                productImage.setProduct(existingProduct);
                newProductImages.add(productImage);
            }
            productImageRepository.saveAll(newProductImages);

            if (existingProduct.getProductImages() == null) {
                existingProduct.setProductImages(new ArrayList<>());
            }
            existingProduct.getProductImages().addAll(newProductImages);
        }
        Product updatedProduct = productRepository.save(existingProduct);
        return convertProduct.convertToCreateAndUpdateProductResponse(updatedProduct);
    }

    @Transactional
    public boolean deleteProduct(Integer id) {
        return productRepository.findById(id)
                .map(product -> {
                    if (product.getThumbnail() != null && !product.getThumbnail().isEmpty()) {
                        firebaseService.deleteFileByUrl(product.getThumbnail());
                    }
                    if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
                        for (ProductImage img : product.getProductImages()) {
                            firebaseService.deleteFileByUrl(img.getImageUrl());
                        }
                        productImageRepository.deleteAll(product.getProductImages());
                    }
                    productRepository.delete(product);
                    return true;
                })
                .orElse(false);
    }
}