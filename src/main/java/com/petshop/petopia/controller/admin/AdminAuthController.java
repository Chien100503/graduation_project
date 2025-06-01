package com.petshop.petopia.controller.admin;

import com.petshop.petopia.dto.request.auth.LoginRequest;
import com.petshop.petopia.dto.response.auth.AdminLoginResponse;
import com.petshop.petopia.dto.response.auth.LoginResponse;
import com.petshop.petopia.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAuthController {
    private final AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<AdminLoginResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.loginAdmin(request));
    }
}
