package com.petshop.petopia.dto.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AddressRequest {
    private String name;
    private String phone;
    private String fullAddress;
    private Boolean isDefault;
}