package com.petshop.petopia.service.admin;

import com.petshop.petopia.dto.request.product.CreateProductRequest;
import com.petshop.petopia.dto.response.product.ProductResponse;
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
import java.util.stream.Collectors;

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
    public ProductResponse createProduct(CreateProductRequest req) throws IOException {
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

        if (req.getThumnail() != null && !req.getThumnail().isEmpty()) {
            String thumnailUrl = firebaseService.uploadImageThumnail(req.getThumnail());
            product.setThumnail(thumnailUrl);
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
            savedProduct.setProductImages(productImages); // Corrected variable name here.
        }

        return convertProduct.convertToResponse(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(Integer id, CreateProductRequest req) throws IOException {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với ID: " + id));

        Brand brand = brandRepository.findByName(req.getBrandName())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thương hiệu: " + req.getBrandName()));
        existingProduct.setBrand(brand);

        ProductCategory category = productCategoryRepository.findByName(req.getProductCategoryName())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục sản phẩm: " + req.getProductCategoryName()));
        existingProduct.setPrCategory(category);

        Type type = typeRepository.findByNameAndProductCategory(req.getTypeName(), category)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy loại '" + req.getTypeName() + "' trong danh mục '" + req.getProductCategoryName() + "'."));
        existingProduct.setType(type);

        existingProduct.setName(req.getName());
        existingProduct.setDescription(req.getDescription());
        existingProduct.setSize(req.getSize());
        existingProduct.setWeight(req.getWeight());
        existingProduct.setPrice(req.getPrice());
        existingProduct.setStockQuantity(req.getStockQuantity());
        existingProduct.setExpirationDate(req.getExpirationDate());

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
            existingProduct.getProductImages().addAll(newProductImages);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return convertProduct.convertToResponse(updatedProduct);
    }

    @Transactional
    public boolean deleteProduct(Integer id) {
        return productRepository.findById(id)
                .map(product -> {
                    productImageRepository.deleteAll(product.getProductImages());
                    productRepository.delete(product);
                    return true;
                })
                .orElse(false);
    }
}