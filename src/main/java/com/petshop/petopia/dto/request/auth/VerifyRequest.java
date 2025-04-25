package com.petshop.petopia.dto.request.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyRequest {
    private String code;
}
