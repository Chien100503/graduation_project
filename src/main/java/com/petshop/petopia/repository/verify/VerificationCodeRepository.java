package com.petshop.petopia.repository.verify;

import com.petshop.petopia.model.VerificationCode;
import com.petshop.petopia.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Integer> {

    // Tìm mã xác thực theo đối tượng User
    Optional<VerificationCode> findByUser(User user);

    // Xóa mã xác thực theo đối tượng User
    void deleteByUser(User user);

    // Tìm mã xác thực theo userId (uid của User)
    Optional<VerificationCode> findByUser_Uid(Integer userUid);

    // Xóa mã xác thực theo userId (uid của User)
    @Modifying
    @Query("DELETE FROM VerificationCode v WHERE v.user.uid = :userId")
    void deleteByUserId(@Param("userId") Integer userId);

    void deleteAllByExpiryTimeLessThan(Long expiryTime);
}
