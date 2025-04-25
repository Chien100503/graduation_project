package com.petshop.petopia.repository.product;


import com.petshop.petopia.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    // Bạn có thể thêm các truy vấn tùy chỉnh ở đây nếu cần
}