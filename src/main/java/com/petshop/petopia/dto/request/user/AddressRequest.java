package com.petshop.petopia.dto.request.user;

import lombok.Data;

@Data
public class AddressRequest {
    private String name;
    private String recipientName;
    private String phone;
    private String fullAddress;
    private Boolean isDefault;
}