package com.petshop.petopia.dto.request.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResetPasswordRequest {
    private String token;
    private String password;
    private String confirmPassword;
}
