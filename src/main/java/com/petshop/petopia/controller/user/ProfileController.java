package com.petshop.petopia.controller.user;

import com.petshop.petopia.dto.request.user.UpdateProfileRequest;
import com.petshop.petopia.dto.response.auth.ProfileResponse;
import com.petshop.petopia.security.JwtService;
import com.petshop.petopia.service.auth.AuthService;
import com.petshop.petopia.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProfileController {

    private final AuthService authService;
    private final AuthService.VerificationCodeService verificationCodeService;
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
