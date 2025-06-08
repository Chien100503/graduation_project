package com.petshop.petopia.dto.request.order;

import com.petshop.petopia.component.Global;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
public class CreateOrderRequest {
    private Integer addressId;
    private Global.PaymentMethod paymentMethod;
}