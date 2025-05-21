package com.petshop.petopia.dto.request.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {
    private String name;
    private String firstName;
    private String lastName;
    private String phone;
}
