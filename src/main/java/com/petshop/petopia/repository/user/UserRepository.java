package com.petshop.petopia.repository.user;

import com.petshop.petopia.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    @Modifying
    @Query("DELETE FROM User u WHERE u.id = :userId")
    void deleteByUserId(@Param("userId") int userId);
}