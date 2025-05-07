package com.petshop.petopia.dto.request.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String password;
    private String confirmPassword;
}