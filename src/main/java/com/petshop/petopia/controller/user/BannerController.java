package com.petshop.petopia.controller.user;

import com.petshop.petopia.dto.response.banner.BannerDetail;
import com.petshop.petopia.dto.response.banner.BannerInfo;
import com.petshop.petopia.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @GetMapping
    public ResponseEntity<List<BannerInfo>> getAllBanners() {
        List<BannerInfo> banners = bannerService.getAllBanners();
        return new ResponseEntity<>(banners, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BannerDetail> getBannerById(@PathVariable Integer id) {
        Optional<BannerDetail> bannerDetail = bannerService.getBannerById(id);
        return bannerDetail.map(detail -> new ResponseEntity<>(detail, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
