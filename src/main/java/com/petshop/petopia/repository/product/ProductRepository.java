package com.petshop.petopia.repository.product;


import com.petshop.petopia.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    List<Product> findByBannerId(Integer bannerId);
    List<Product> findByPrCategory_Name(String name);
    List<Product> findByBrand_Name(String name);
    List<Product> findByType_Name(String name);
    List<Product> findByPrCategory_IdAndType_Id(Integer categoryId, Integer typeId);
}