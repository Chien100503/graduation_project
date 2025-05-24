package com.petshop.petopia.dto.request.banner;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
public class CUBannerRequest {
    private MultipartFile file;
    private Double salePercent;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date endDate;
}
