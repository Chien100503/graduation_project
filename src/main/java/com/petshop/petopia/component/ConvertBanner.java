package com.petshop.petopia.component;

import com.petshop.petopia.dto.response.banner.CUBannerResponse;
import com.petshop.petopia.model.sale.Banner;
import org.springframework.stereotype.Component;

@Component
public class ConvertBanner {
    public CUBannerResponse mapBannerToCUBannerResponse(Banner banner) {
        return new CUBannerResponse(
                banner.getId(),
                banner.getImage(),
                banner.getSalePercent(),
                banner.getStartDate(),
                banner.getEndDate()
        );
    }
}
