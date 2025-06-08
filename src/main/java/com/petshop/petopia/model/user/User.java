package com.petshop.petopia.model.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.petshop.petopia.model.review.ProductRating;
import com.petshop.petopia.model.review.Review;
import com.petshop.petopia.model.order.Order;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String password;
    private Boolean isActive;
    private String avatar;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    @JsonManagedReference
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Order> orders;


    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<ProductRating> productRatings;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Review> reviews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}