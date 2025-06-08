package com.petshop.petopia.service;

import com.petshop.petopia.component.CalculatePrice;
import com.petshop.petopia.component.ConvertBanner;
import com.petshop.petopia.dto.response.banner.*;
import com.petshop.petopia.model.pet.PetImage;
import com.petshop.petopia.model.product.ProductImage;
import com.petshop.petopia.repository.BannerRepository;
import com.petshop.petopia.repository.pet.PetRepository;
import com.petshop.petopia.repository.product.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;
    private final PetRepository petRepository;
    private final ProductRepository productRepository;
    private final FirebaseService firebaseService;
    private final CalculatePrice calculatePrice;
    private final ConvertBanner convertBanner;

    public List<BannerInfo> getAllBanners() {
        return bannerRepository.findAll().stream()
                .map(banner -> new BannerInfo(banner.getId(), banner.getImage()))
                .collect(Collectors.toList());
    }

    public Optional<BannerDetail> getBannerById(Integer id) {
        return bannerRepository.findById(id).map(banner -> {
            List<PetWithDiscount> pets = petRepository.findByBannerId(banner.getId()).stream()
                    .map(pet -> {
                        String firstImageUrl = pet.getPetImages().stream()
                                .findFirst()
                                .map(PetImage::getImageUrl)
                                .orElse(null);

                        return new PetWithDiscount(
                                pet.getId(),
                                firstImageUrl,
                                pet.getName(),
                                pet.getPrice(),
                                calculatePrice.calculatePriceDiscount(pet.getPrice(), banner.getSalePercent())
                        );
                    })
                    .collect(Collectors.toList());

            List<ProductWithDiscount> products = productRepository.findByBannerId(banner.getId()).stream()
                    .map(product -> {
                        String firstImageUrl = product.getProductImages().stream()
                                .findFirst()
                                .map(ProductImage::getImageUrl)
                                .orElse(null);

                        return new ProductWithDiscount(
                                product.getId(),
                                firstImageUrl,
                                product.getName(),
                                product.getPrice(),
                                calculatePrice.calculatePriceDiscount(product.getPrice(), banner.getSalePercent())
                        );
                    })
                    .collect(Collectors.toList());

            return new BannerDetail(banner, products, pets);
        });
    }




}