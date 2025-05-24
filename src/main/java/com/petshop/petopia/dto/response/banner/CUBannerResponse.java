package com.petshop.petopia.dto.response.banner;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class CUBannerResponse {
    private Integer id;
    private String image;
    private Double salePercent;
    private Date startDate;
    private Date endDate;
}
