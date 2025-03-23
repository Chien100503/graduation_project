package com.petshop.petopia.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Users {
    @Id
    private long id;

    private String userName;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
}
