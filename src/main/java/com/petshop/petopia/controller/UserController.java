package com.petshop.petopia.controller;

import com.petshop.petopia.dto.request.auth.LoginRequest;
import com.petshop.petopia.dto.request.auth.RegisterRequest;
import com.petshop.petopia.dto.request.auth.VerifyRequest;
import com.petshop.petopia.dto.response.auth.LoginResponse;
import com.petshop.petopia.dto.response.MessageResponse;
import com.petshop.petopia.dto.response.auth.RegisterResponse;
import com.petshop.petopia.service.AuthService;
import com.petshop.petopia.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final VerificationCodeService verificationCodeService;

    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponse> login(@ModelAttribute LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(value = "/register")
    public ResponseEntity<RegisterResponse> register(@ModelAttribute RegisterRequest registerRequest) {
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu không khớp!");
        }

        RegisterResponse response = authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/verify")
    public MessageResponse verify(@ModelAttribute VerifyRequest req, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        authService.verify(req, token);

        return new MessageResponse("Xác thực thành công");
    }

    @PostMapping("/resend")
    public MessageResponse resendVerificationCode(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token không hợp lệ");
        }

        String token = authHeader.replace("Bearer ", "");

        try {
            // Gửi lại mã xác thực cho người dùng
            verificationCodeService.resendCode(token);
            return new MessageResponse("Mã xác thực đã được gửi lại");
        } catch (Exception e) {
            throw new RuntimeException("Có lỗi xảy ra khi gửi mã xác thực: " + e.getMessage());
        }
    }


}
