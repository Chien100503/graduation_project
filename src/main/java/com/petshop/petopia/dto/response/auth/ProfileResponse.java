package com.petshop.petopia.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileResponse {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String avatar;
}
