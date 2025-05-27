package com.petshop.petopia.dto.response.user;

import lombok.Data;

@Data
public class AddressResponse {
    private Integer id;
    private String name;
    private String phone;
    private String fullAddress;
    private boolean isDefault;
}