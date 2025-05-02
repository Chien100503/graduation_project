package com.petshop.petopia.repository.product;

import com.petshop.petopia.model.product.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeRepository extends JpaRepository<Type, Integer> {
    Optional<Type> findByName(String name);
}