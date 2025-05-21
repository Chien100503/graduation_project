package com.petshop.petopia.controller;

import com.petshop.petopia.dto.request.auth.LoginRequest;
import com.petshop.petopia.dto.request.auth.RegisterRequest;
import com.petshop.petopia.dto.request.auth.VerifyRequest;
import com.petshop.petopia.dto.request.user.UpdateProfileRequest;
import com.petshop.petopia.dto.response.auth.LoginResponse;
import com.petshop.petopia.dto.response.MessageResponse;
import com.petshop.petopia.dto.response.auth.ProfileResponse;
import com.petshop.petopia.dto.response.auth.RegisterResponse;
import com.petshop.petopia.model.user.User;
import com.petshop.petopia.security.JwtService;
import com.petshop.petopia.service.AuthService;
import com.petshop.petopia.service.UserService;
import com.petshop.petopia.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final VerificationCodeService verificationCodeService;
    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping("/profile")
    public ProfileResponse profile(@RequestHeader("Authorization") String token) {
        Integer userId = jwtService.extractUserId(token);
        return userService.profile(userId);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String token,
            @ModelAttribute UpdateProfileRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatarFile
    ) {
        Integer userId = jwtService.extractUserId(token);
        ProfileResponse updatedProfile = userService.updateProfile(userId, request, avatarFile);
        return ResponseEntity.ok(updatedProfile);
    }
}
