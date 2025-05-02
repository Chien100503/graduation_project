package com.petshop.petopia.service;

import com.petshop.petopia.dto.request.admin.ProductCreateRequest;
import com.petshop.petopia.dto.response.ProductResponse;
import com.petshop.petopia.model.product.*;
import com.petshop.petopia.repository.product.*;
import com.petshop.petopia.util.ConvertProduct;
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
public class ProductService {

    private final FirebaseService firebaseService;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ConvertProduct convertProduct;
    private final BrandRepository brandRepository;
    private final TypeRepository typeRepository;
    private final ProductImageRepository productImageRepository;

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest req) throws IOException {

        // 1. Tìm hoặc tạo Brand
        Brand brand;
        if (req.getBrandName() != null && !req.getBrandName().isEmpty()) {
            Optional<Brand> brandOpt = brandRepository.findByName(req.getBrandName());
            brand = brandOpt.orElseGet(() -> {
                Brand newBrand = new Brand();
                newBrand.setName(req.getBrandName());
                newBrand.setCreatedAt(new Date());
                return brandRepository.save(newBrand);
            });
        } else {
            brand = null;
        }

        // 2. Tìm hoặc tạo Type
        Type type;
        if (req.getTypeName() != null && !req.getTypeName().isEmpty()) {
            Optional<Type> typeOpt = typeRepository.findByName(req.getTypeName());
            type = typeOpt.orElseGet(() -> {
                Type newType = new Type();
                newType.setName(req.getTypeName());
                newType.setCreatedAt(new Date());
                return typeRepository.save(newType);
            });
        } else {
            type = null;
        }

        // 3. Tìm hoặc tạo ProductCategory
        Optional<ProductCategory> categoryOpt = productCategoryRepository.findByName(req.getProductCategoryName());
        ProductCategory category = categoryOpt.orElseGet(() -> {
            ProductCategory newCategory = new ProductCategory();
            newCategory.setName(req.getProductCategoryName());
            newCategory.setDescription("No description available");
            newCategory.setCreatedAt(new Date());
            return productCategoryRepository.save(newCategory);
        });

        // 4. Tạo Product
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


        // 5. Lưu Product (để có ID cho ProductImage)
        Product savedProduct = productRepository.save(product);


        // 6. Xử lý hình ảnh: Tải lên, tạo ProductImage objects, và lưu
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


        // 7. Trả về response
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

            // 1. Tìm hoặc tạo Brand
            Brand brand;
            if (req.getBrandName() != null && !req.getBrandName().isEmpty()) {
                Optional<Brand> brandOpt = brandRepository.findByName(req.getBrandName());
                brand = brandOpt.orElseGet(() -> {
                    Brand newBrand = new Brand();
                    newBrand.setName(req.getBrandName());
                    newBrand.setCreatedAt(new Date());
                    return brandRepository.save(newBrand);
                });
            } else {
                brand = existingProduct.getBrand(); // Keep the existing brand
            }

            // 2. Tìm hoặc tạo Type
            Type type;
            if (req.getTypeName() != null && !req.getTypeName().isEmpty()) {
                Optional<Type> typeOpt = typeRepository.findByName(req.getTypeName());
                type = typeOpt.orElseGet(() -> {
                    Type newType = new Type();
                    newType.setName(req.getTypeName());
                    newType.setCreatedAt(new Date());
                    return typeRepository.save(newType);
                });
            } else {
                type = existingProduct.getType(); // Keep the existing type
            }


            existingProduct.setName(req.getName());
            existingProduct.setBrand(brand);
            existingProduct.setDescription(req.getDescription());
            existingProduct.setType(type);
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

            // Xử lý hình ảnh: Tải lên, tạo ProductImage objects, và lưu (Xóa cũ và thêm mới)
            if (req.getFile() != null && !req.getFile().isEmpty()) {
                // Xóa các hình ảnh cũ liên quan đến sản phẩm này
                productImageRepository.deleteAll(existingProduct.getProductImages());
                List<ProductImage> newProductImages = new ArrayList<>();
                for (MultipartFile file : req.getFile()) {
                    String imageUrl = firebaseService.uploadImageProduct(file);
                    ProductImage productImage = new ProductImage();
                    productImage.setImageUrl(imageUrl);
                    productImage.setProduct(existingProduct);
                    newProductImages.add(productImage);
                }
                productImageRepository.saveAll(newProductImages);
                existingProduct.setProductImages(newProductImages); // Cập nhật danh sách hình ảnh mới
            }


            Product updatedProduct = productRepository.save(existingProduct);
            return convertProduct.convertToResponse(updatedProduct);
        }
        return null;
    }

    @Transactional
    public boolean deleteProduct(Integer id) {
        return productRepository.findById(id)
                .map(product -> {
                    // Xóa các hình ảnh liên quan trước khi xóa sản phẩm
                    productImageRepository.deleteAll(product.getProductImages());
                    productRepository.delete(product);
                    return true;
                })
                .orElse(false);
    }
}

