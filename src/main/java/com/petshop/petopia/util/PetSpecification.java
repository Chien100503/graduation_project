package com.petshop.petopia.util;

import com.petshop.petopia.model.pet.Breed;
import com.petshop.petopia.model.pet.Pet;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class PetSpecification {

    public static Specification<Pet> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isEmpty()) {
                return cb.conjunction();
            }
            return cb.or(
                    cb.like(root.get("name"), "%" + keyword + "%"),
                    cb.like(root.get("description"), "%" + keyword + "%")
            );
        };
    }

    public static Specification<Pet> hasPetCategory(Integer petCategoryId) {
        return (root, query, cb) ->
                petCategoryId == null ? cb.conjunction() :
                        cb.equal(root.get("petCategory").get("id"), petCategoryId);
    }

    public static Specification<Pet> hasBreed(Integer breedId) {
        return (root, query, cb) ->
                breedId == null ? cb.conjunction() :
                        cb.equal(root.get("breed").get("id"), breedId);
    }

    public static Specification<Pet> isBreedInPetCategory(Integer petCategoryId) {
        return (root, query, cb) -> {
            if (petCategoryId == null) {
                return cb.conjunction();
            }
            Join<Pet, Breed> breedJoin = root.join("breed");
            return cb.equal(breedJoin.get("petCategory").get("id"), petCategoryId);
        };
    }

    public static Specification<Pet> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) {
                return cb.conjunction();
            }
            if (minPrice == null) {
                return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
            }
            if (maxPrice == null) {
                return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
            }
            return cb.between(root.get("price"), minPrice, maxPrice);
        };
    }
}