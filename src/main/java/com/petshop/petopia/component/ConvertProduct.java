package com.petshop.petopia.component;

import com.petshop.petopia.dto.response.product.CreateProductResponse;
import com.petshop.petopia.dto.response.product.GetProductDetailResponse;
import com.petshop.petopia.dto.response.product.GetAllProductResponse;
import com.petshop.petopia.dto.response.product.ProductCategoryResponse;
import com.petshop.petopia.model.product.Product;
import com.petshop.petopia.model.product.ProductImage;
import com.petshop.petopia.model.review.Rating;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ConvertProduct {
    private final CalculatePrice calculatePrice;

    public GetAllProductResponse convertToGetAllProductResponse(Product product) {
        BigDecimal percentDiscount = BigDecimal.ZERO;
        BigDecimal priceDiscount = product.getPrice();

        if (product.getBanner() != null && product.getBanner().getSalePercent() != null) {
            BigDecimal salePercentFromBanner = product.getBanner().getSalePercent();

            percentDiscount = calculatePrice.getPercentDiscount(salePercentFromBanner);

            if (percentDiscount.compareTo(BigDecimal.ZERO) > 0) {
                priceDiscount = calculatePrice.calculatePriceDiscount(product.getPrice(), percentDiscount);
            }
        }

        double averageRate = 0.0;
        Rating productRating = product.getRating();

        if (productRating != null) {
            int totalRatings = CalculateRating.calculateTotalRatings(productRating);

            if (totalRatings > 0) {
                averageRate = CalculateRating.calculateAverageRating(product.getRating());
                averageRate = CalculateRating.roundToTwoDecimalPlaces(averageRate);
            }
        }

        return new GetAllProductResponse(
                product.getId(),
                product.getName(),
                product.getThumbnail(),
                averageRate,
                product.getDescription(),
                product.getBrand().getName(),
                calculatePrice.roundBigDecimal(percentDiscount),
                product.getPrice(),
                calculatePrice.roundBigDecimal(priceDiscount)
        );
    }

    public GetProductDetailResponse convertToGetProductDetailResponse(Product product) {
        BigDecimal percentDiscount = BigDecimal.ZERO;
        BigDecimal priceDiscount = product.getPrice();

        if (product.getBanner() != null && product.getBanner().getSalePercent() != null) {
            BigDecimal salePercentFromBanner = product.getBanner().getSalePercent();
            percentDiscount = calculatePrice.getPercentDiscount(salePercentFromBanner);

            if (percentDiscount.compareTo(BigDecimal.ZERO) > 0) {
                priceDiscount = calculatePrice.calculatePriceDiscount(product.getPrice(), percentDiscount);
            }
        }

        double averageRate = 0.0;
        Rating productRating = product.getRating();

        if (productRating != null) {
            int totalRatings = CalculateRating.calculateTotalRatings(productRating);

            if (totalRatings > 0) {
                averageRate = CalculateRating.calculateAverageRating(productRating);
                averageRate = CalculateRating.roundToTwoDecimalPlaces(averageRate);
            }
        }

        String categoryName = (product.getPrCategory() != null) ? product.getPrCategory().getName() : null;
        String typeName = (product.getType() != null) ? product.getType().getName() : null;

        List<String> imageUrls = Collections.emptyList();
        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
            imageUrls = product.getProductImages().stream()
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList());
        }

        String expirationDateString = product.getExpirationDate();


        return new GetProductDetailResponse(
                product.getId(),
                product.getName(),
                categoryName,
                typeName,
                product.getDescription(),
                averageRate,
                calculatePrice.roundBigDecimal(percentDiscount),
                product.getPrice(),
                calculatePrice.roundBigDecimal(priceDiscount),
                imageUrls,
                product.getStockQuantity(),
                product.getSize(),
                product.getWeight(),
                expirationDateString
        );
    }

    public CreateProductResponse convertToCreateAndUpdateProductResponse(Product product) {
        List<String> imageUrls = product.getProductImages() != null
                ? product.getProductImages().stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList())
                : List.of();

        return new CreateProductResponse(
                product.getId(),
                product.getName(),
                product.getPrCategory().getName(),
                product.getBrand() != null ? product.getBrand().getName() : null,
                product.getType() != null ? product.getType().getName() : null,
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getSize(),
                product.getWeight(),
                product.getExpirationDate(),
                imageUrls,
                product.getThumbnail()
        );
    }

    public ProductCategoryResponse convertToProductCategoryResponse(Product product) {
        return new ProductCategoryResponse(
                product.getId(),
                product.getName(),
                product.getProductImages() != null && !product.getProductImages().isEmpty()
                        ? product.getProductImages().getFirst().getImageUrl() : null
        );
    }
}
