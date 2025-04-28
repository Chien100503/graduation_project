package com.petshop.petopia.service;

import com.petshop.petopia.dto.request.admin.ProductCreateRequest;
import com.petshop.petopia.dto.response.ProductResponse;
import com.petshop.petopia.model.product.Product;
import com.petshop.petopia.model.product.ProductCategory;
import com.petshop.petopia.repository.product.ProductCategoryRepository;
import com.petshop.petopia.repository.product.ProductRepository;
import com.petshop.petopia.util.ConvertProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final FirebaseService firebaseService;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ConvertProduct convertProduct;

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest req) throws IOException {
        String imageUrl = firebaseService.uploadImageProduct(req.getFile());

        Product product = new Product();
        product.setName(req.getName());
        product.setBrand(req.getBrand());
        product.setDescription(req.getDescription());
        product.setType(req.getType());
        product.setSize(req.getSize());
        product.setWeight(req.getWeight());
        product.setPrice(req.getPrice());
        product.setStockQuantity(req.getStockQuantity());
        product.setExpirationDate(req.getExpirationDate());
        product.setImages(imageUrl);

        Optional<ProductCategory> categoryOpt = productCategoryRepository.findByName(req.getProductCategoryName());
        ProductCategory category = categoryOpt.orElseGet(() -> {
            ProductCategory newCategory = new ProductCategory();
            newCategory.setName(req.getProductCategoryName());
            newCategory.setDescription("No description available");
            newCategory.setCreatedAt(new Date());
            return productCategoryRepository.save(newCategory);
        });
        product.setPrCategory(category);

        Product savedProduct = productRepository.save(product);
        return convertProduct.convertToResponse(savedProduct);
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(convertProduct::convertToResponse)
                .collect(Collectors.toList());
    }

    public Optional<ProductResponse> getProductById(Integer id) {
        return productRepository.findById(id)
                .map(convertProduct::convertToResponse);
    }

    @Transactional
    public ProductResponse updateProduct(Integer id, ProductCreateRequest req) throws IOException {
        Optional<Product> existingProductOpt = productRepository.findById(id);
        if (existingProductOpt.isPresent()) {
            Product existingProduct = existingProductOpt.get();

            if (req.getFile() != null && !req.getFile().isEmpty()) {
                String imageUrl = firebaseService.uploadImageProduct(req.getFile());
                existingProduct.setImages(imageUrl);
            }

            existingProduct.setName(req.getName());
            existingProduct.setBrand(req.getBrand());
            existingProduct.setDescription(req.getDescription());
            existingProduct.setType(req.getType());
            existingProduct.setSize(req.getSize());
            existingProduct.setWeight(req.getWeight());
            existingProduct.setPrice(req.getPrice());
            existingProduct.setStockQuantity(req.getStockQuantity());
            existingProduct.setExpirationDate(req.getExpirationDate());

            Optional<ProductCategory> categoryOpt = productCategoryRepository.findByName(req.getProductCategoryName());
            ProductCategory category = categoryOpt.orElseGet(() -> {
                ProductCategory newCategory = new ProductCategory();
                newCategory.setName(req.getProductCategoryName());
                newCategory.setDescription("No description available");
                newCategory.setCreatedAt(new Date());
                return productCategoryRepository.save(newCategory);
            });
            existingProduct.setPrCategory(category);

            Product updatedProduct = productRepository.save(existingProduct);
            return convertProduct.convertToResponse(updatedProduct);
        }
        return null;
    }

    @Transactional
    public boolean deleteProduct(Integer id) {
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.delete(product);
                    return true;
                })
                .orElse(false);
    }
}