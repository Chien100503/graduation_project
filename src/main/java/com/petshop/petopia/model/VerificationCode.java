package com.petshop.petopia.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "verification_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;

    private Long expiryTime;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "uid", unique = true)
    private User user;
}
