//package com.petshop.petopia.service;
//
//import com.petshop.petopia.dto.request.admin.PetCreateRequest;
//import com.petshop.petopia.model.product.Pet;
//import com.petshop.petopia.model.product.Product;
//import com.petshop.petopia.model.product.ProductCategory;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.Date;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class ProductService {
//
//    private final FirebaseService firebaseService;
//
//    public Product createProduct(PetCreateRequest req) throws IOException {
//        String imageUrl = firebaseService.uploadImageProduct(req.getFile());
//
//        Product product = new Product();
//        product.setName(req.getName());
//        product.setBreed(req.getBreed());
//        product.setAge(req.getAge());
//        product.setGender(req.getGender());
//        product.setSize(req.getSize());
//        product.setWeight(req.getWeight());
//        product.setColor(req.getColor());
//        product.setPrice(req.getPrice());
//        pet.setStatus(req.getStatus());
//        pet.setHealthStatus(req.getHealthStatus());
//        pet.setDescription(req.getDescription());
//        pet.setImg(imageUrl); // gán URL ảnh
//
//        // Tìm ProductCategory theo tên
//        Optional<ProductCategory> categoryOpt = categoryRepository.findByName(req.getProductCategoryName());
//        if (categoryOpt.isEmpty()) {
//            ProductCategory newCategory = new ProductCategory();
//            newCategory.setName(req.getProductCategoryName());
//            newCategory.setDescription("No description available");
//            newCategory.setCreatedAt(new Date());
//            categoryRepository.save(newCategory);
//            pet.setPrCategory(newCategory);
//        } else {
//            pet.setPrCategory(categoryOpt.get());
//        }
//
//        return petRepository.save(pet);
//    }
//}
