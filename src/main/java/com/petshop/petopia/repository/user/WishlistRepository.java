package com.petshop.petopia.repository.user;

import com.petshop.petopia.model.user.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<WishList, Integer> {
    List<WishList> findByUserId(Integer userId);

    boolean existsByUserIdAndPetId(Integer userId, Integer petId);

    boolean existsByUserIdAndProductId(Integer userId, Integer productId);

    void deleteByUserIdAndPetId(Integer userId, Integer petId);

    void deleteByUserIdAndProductId(Integer userId, Integer productId);
}