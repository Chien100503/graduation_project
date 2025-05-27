package com.petshop.petopia.repository.product;

import com.petshop.petopia.model.product.ProductCategory;
import com.petshop.petopia.model.product.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypeRepository extends JpaRepository<Type, Integer> {
    Optional<Type> findByName(String name);
    Optional<Type> findByNameAndProductCategory(String name, ProductCategory productCategory);
    Optional<List<Type>> findByProductCategory_Id(Integer categoryId);
}