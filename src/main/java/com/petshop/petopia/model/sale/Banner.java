package com.petshop.petopia.model.sale;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "banners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String image;

    @NotNull(message = "Phần trăm giảm giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Phần trăm giảm giá phải lớn hơn hoặc bằng 0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Phần trăm giảm giá phải nhỏ hơn hoặc bằng 100")
    @Column(precision = 5, scale = 2)
    private BigDecimal salePercent;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    private Boolean isActive = true;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = new Date();
    }

    @PrePersist
    public void setCreatedAtAndUpdatedAt() {
        if (this.createdAt == null) {
            this.createdAt = new Date();
        }
        if (this.updatedAt == null) {
            this.updatedAt = new Date();
        }
    }

    @AssertTrue(message = "Ngày bắt đầu phải trước ngày kết thúc")
    public boolean isStartDateBeforeEndDate() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return startDate.before(endDate);
    }
}