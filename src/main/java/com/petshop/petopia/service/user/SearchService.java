//package com.petshop.petopia.service.user;
//
//import com.petshop.petopia.component.ConvertPet;
//import com.petshop.petopia.component.ConvertProduct;
//import com.petshop.petopia.dto.request.user.SearchRequest;
//import com.petshop.petopia.dto.response.user.SearchResponse;
//import com.petshop.petopia.dto.response.pet.GetAllPetResponse;
//import com.petshop.petopia.dto.response.product.GetAllProductResponse;
//import com.petshop.petopia.model.pet.Pet;
//import com.petshop.petopia.model.product.Product;
//import com.petshop.petopia.repository.pet.PetRepository;
//import com.petshop.petopia.repository.product.ProductRepository;
//import com.petshop.petopia.repository.user.WishlistRepository;
//import jakarta.persistence.criteria.Predicate;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class SearchService {
//
//    private final PetRepository petRepository;
//    private final ProductRepository productRepository;
//    private final ConvertPet convertPet;
//    private final ConvertProduct convertProduct;
//    private final WishlistRepository wishlistRepository;
//
//    @Transactional(readOnly = true)
//    public SearchResponse searchAll(Integer userId, SearchRequest request) {
//        String rawKeyword = request.getKeyword();
//        String keyword = (rawKeyword == null || rawKeyword.isBlank()) ? null : rawKeyword.toLowerCase().trim();
//
//        if (keyword == null) {
//            return new SearchResponse(List.of(), List.of());
//        }
//
//        String keywordLike = "%" + keyword + "%";
//
//        Specification<Pet> petSpec = (root, query, cb) -> {
//            List<Predicate> predicates = new ArrayList<>();
//
//            predicates.add(cb.isTrue(root.get("status")));
//
//            Predicate breedPredicate = cb.like(cb.lower(root.join("breed").get("name")), keywordLike);
//            Predicate petNamePredicate = cb.like(cb.lower(root.get("name")), keywordLike);
//
//            predicates.add(cb.or(breedPredicate, petNamePredicate));
//
//            return cb.and(predicates.toArray(new Predicate[0]));
//        };
//
//        Specification<Product> productSpec = (root, query, cb) -> {
//            List<Predicate> predicates = new ArrayList<>();
//
//            Predicate brandPredicate = cb.like(cb.lower(root.join("brand").get("name")), keywordLike);
//            Predicate typePredicate = cb.like(cb.lower(root.join("type").get("name")), keywordLike);
//            Predicate productNamePredicate = cb.like(cb.lower(root.get("name")), keywordLike);
//
//            predicates.add(cb.or(brandPredicate, typePredicate, productNamePredicate));
//
//            return cb.and(predicates.toArray(new Predicate[0]));
//        };
//
//        // Lấy dữ liệu
//        List<Pet> pets = petRepository.findAll(petSpec);
//        List<Product> products = productRepository.findAll(productSpec);
//
//        List<Integer> wishlistPetIds = wishlistRepository.existsByUserIdAndPetId(userId, );
//        List<Integer> wishlistProductIds = wishlistRepository.existsByUserIdAndProductId(userId);
//
//        // Convert và truyền isInWishlist
//        List<GetAllPetResponse> petResponses = pets.stream()
//                .map(pet -> {
//                    boolean isInWishlist = wishlistPetIds.contains(pet.getId());
//                    return convertPet.convertToGetAllPetResponse(pet, isInWishlist);
//                })
//                .toList();
//
//        List<GetAllProductResponse> productResponses = products.stream()
//                .map(product -> {
//                    boolean isInWishlist = wishlistProductIds.contains(product.getId());
//                    return convertProduct.convertToGetProductResponse(product);
//                })
//                .toList();
//
//        return new SearchResponse(petResponses, productResponses);
//    }
//}
