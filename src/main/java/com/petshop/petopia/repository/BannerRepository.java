package com.petshop.petopia.repository;

import com.petshop.petopia.model.sale.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BannerRepository extends JpaRepository<Banner,Integer> {
}
