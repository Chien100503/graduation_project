package com.petshop.petopia.dto.response.banner;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
public class CUBannerResponse {
    private Integer id;
    private String image;
    private BigDecimal salePercent;
    private Date startDate;
    private Date endDate;
}
