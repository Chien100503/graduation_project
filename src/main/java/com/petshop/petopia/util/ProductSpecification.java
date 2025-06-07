package com.petshop.petopia.util;

import com.petshop.petopia.model.product.Product;
import com.petshop.petopia.model.product.Type;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> hasKeyword(String keyword) {
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

    public static Specification<Product> hasProductCategory(Integer productCategoryId) {
        return (root, query, cb) ->
                productCategoryId == null ? cb.conjunction() :
                        cb.equal(root.get("productCategory").get("id"), productCategoryId);
    }

    public static Specification<Product> hasBrand(Integer brandId) {
        return (root, query, cb) ->
                brandId == null ? cb.conjunction() :
                        cb.equal(root.get("brand").get("id"), brandId);
    }

    public static Specification<Product> hasType(Integer typeId) {
        return (root, query, cb) ->
                typeId == null ? cb.conjunction() :
                        cb.equal(root.get("type").get("id"), typeId);
    }

    public static Specification<Product> isTypeInProductCategory(Integer productCategoryId) {
        return (root, query, cb) -> {
            if (productCategoryId == null) {
                return cb.conjunction();
            }
            Join<Product, Type> typeJoin = root.join("type");
            return cb.equal(typeJoin.get("productCategory").get("id"), productCategoryId);
        };
    }

    public static Specification<Product> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
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