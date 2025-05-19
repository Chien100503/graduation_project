package com.petshop.petopia.dto.request.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ForgetPasswordRequest {
    private String email;
}
