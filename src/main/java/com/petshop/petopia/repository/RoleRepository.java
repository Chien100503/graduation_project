package com.petshop.petopia.repository;

import com.petshop.petopia.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String roleName);
    Set<Role> findByUsers_Uid(Integer uid);
    @Query("SELECT r FROM Role r JOIN r.users u WHERE u.email = :email")
    List<Role> findRolesByEmail(@Param("email") String email);
}