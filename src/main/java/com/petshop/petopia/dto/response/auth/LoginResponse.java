package com.petshop.petopia.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private Integer id;
    private String token;
    private Boolean isActive;
}