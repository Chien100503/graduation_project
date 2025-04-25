package com.petshop.petopia.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private Integer id;
    private String token;
    private Boolean isActive;
}
