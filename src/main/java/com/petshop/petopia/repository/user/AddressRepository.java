package com.petshop.petopia.repository.user;

import com.petshop.petopia.model.user.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findByUserId(Integer userId);
    Optional<Address> findByIdAndUserId(Integer id, Integer userId);
    List<Address> findByUserIdAndIsDefaultTrue(Integer userId);
}
