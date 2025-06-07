package com.petshop.petopia.service.category;

import com.petshop.petopia.component.ConvertProduct;
import com.petshop.petopia.dto.request.admin.CreateCategoryRequest;
import com.petshop.petopia.dto.request.category.ProductFilterRequest;
import com.petshop.petopia.dto.request.product.CreateBrandRequest;
import com.petshop.petopia.dto.request.product.CreateTypeRequest;
import com.petshop.petopia.dto.response.admin.CreateCategoryResponse;
import com.petshop.petopia.dto.response.product.*;
import com.petshop.petopia.model.product.Brand;
import com.petshop.petopia.model.product.Product;
import com.petshop.petopia.model.product.ProductCategory;
import com.petshop.petopia.model.product.Type;
import com.petshop.petopia.repository.product.BrandRepository;
import com.petshop.petopia.repository.product.ProductCategoryRepository;
import com.petshop.petopia.repository.product.ProductRepository;
import com.petshop.petopia.repository.product.TypeRepository;
import com.petshop.petopia.service.FirebaseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {
    private final TypeRepository typeRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final BrandRepository brandRepository;
    private final FirebaseService firebaseService;
    private final ConvertProduct convertProduct;

    @Transactional
    public CreateCategoryResponse createProductCategory(CreateCategoryRequest createCategoryRequest) throws IOException {
        Optional<ProductCategory> existingCategory = productCategoryRepository.findByName(createCategoryRequest.getName());
        if (existingCategory.isPresent()) {
            throw new IllegalArgumentException("Tên danh mục sản phẩm '" + createCategoryRequest.getName() + "' đã tồn tại.");
        }

        String imageUrl;
        MultipartFile file = createCategoryRequest.getFile();
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Ảnh danh mục sản phẩm là bắt buộc.");
        } else {
            imageUrl = firebaseService.uploadImageCategory(file);
        }

        ProductCategory category = new ProductCategory();
        category.setName(createCategoryRequest.getName());
        category.setImageUrl(imageUrl);
        ProductCategory savedCategory = productCategoryRepository.save(category);
        return new CreateCategoryResponse(savedCategory.getId(), savedCategory.getName(), savedCategory.getImageUrl());
    }

    @Transactional
    public CreateTypeResponse createType(CreateTypeRequest req) {
        ProductCategory productCategory = productCategoryRepository.findByName(req.getProductCategoryName())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục sản phẩm: " + req.getProductCategoryName()));

        Optional<Type> existingType = typeRepository.findByNameAndProductCategory(req.getName(), productCategory);
        if (existingType.isPresent()) {
            throw new IllegalArgumentException("Loại '" + req.getName() + "' đã tồn tại trong danh mục '" + req.getProductCategoryName() + "'.");
        }

        Type newType = new Type();
        newType.setName(req.getName());
        newType.setProductCategory(productCategory);
        newType.setCreatedAt(new Date());

        Type savedType = typeRepository.save(newType);

        return new CreateTypeResponse(
                savedType.getId(),
                savedType.getName(),
                savedType.getProductCategory().getName()
        );
    }

    @Transactional
    public GetBrandResponse createBrand(CreateBrandRequest req) {
        Optional<Brand> existingBrand = brandRepository.findByName(req.getName());
        if (existingBrand.isPresent()) {
            throw new IllegalArgumentException("Tên thương hiệu '" + req.getName() + "' đã tồn tại.");
        }

        Brand newBrand = new Brand();
        newBrand.setName(req.getName());
        newBrand.setCreatedAt(new Date());
        Brand savedBrand = brandRepository.save(newBrand);

        return new GetBrandResponse(
                savedBrand.getId(),
                savedBrand.getName()
        );
    }

    @Transactional(readOnly = true)
    public List<GetAllProductResponse> filterProducts(ProductFilterRequest filterRequest) {
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
                .map(convertProduct::convertToGetProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductCategoryResponse> getAllProductCategories() {
        return productCategoryRepository.findAll().stream()
                .map(category -> new ProductCategoryResponse(category.getId(), category.getName(), category.getImageUrl()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GetBrandResponse> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(brand -> new GetBrandResponse(brand.getId(), brand.getName()))
                .collect(Collectors.toList());
    }
}
