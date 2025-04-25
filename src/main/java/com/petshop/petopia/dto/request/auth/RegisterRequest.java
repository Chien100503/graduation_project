package com.petshop.petopia.dto.request.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String confirmPassword; // Để xác nhận mật khẩu
}