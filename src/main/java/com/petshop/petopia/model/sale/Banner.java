package com.petshop.petopia.model.sale;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String image;
    private Double salePercent;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    private Boolean isActive = true;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = new Date();
    }

    @PrePersist
    public void setCreatedAt() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @AssertTrue(message = "Banner phải được liên kết với ít nhất một sản phẩm hoặc một thú cưng")
    public boolean isLinkedToProductOrPet() {
        return true;
    }

    @AssertTrue(message = "Ngày bắt đầu phải trước ngày kết thúc")
    public boolean isStartDateBeforeEndDate() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return startDate.before(endDate);
    }

    @AssertTrue(message = "Phần trăm giảm giá phải nằm trong khoảng 0 đến 1")
    public boolean isValidSalePercent() {
        return salePercent == null || (salePercent >= 0 && salePercent <= 1);
    }
}