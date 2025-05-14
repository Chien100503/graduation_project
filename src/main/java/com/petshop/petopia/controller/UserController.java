package com.petshop.petopia.controller;

import com.petshop.petopia.dto.request.auth.LoginRequest;
import com.petshop.petopia.dto.request.auth.RegisterRequest;
import com.petshop.petopia.dto.request.auth.VerifyRequest;
import com.petshop.petopia.dto.response.auth.LoginResponse;
import com.petshop.petopia.dto.response.MessageResponse;
import com.petshop.petopia.dto.response.auth.RegisterResponse;
import com.petshop.petopia.security.JwtService;
import com.petshop.petopia.service.AuthService;
import com.petshop.petopia.service.UserService;
import com.petshop.petopia.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(value = "/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu không khớp!");
        }

        RegisterResponse response = authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/verify")
    public MessageResponse verify(@RequestBody VerifyRequest req, @RequestHeader("Authorization") String authHeader) {
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

    @PostMapping("/user/delete")
    public ResponseEntity<MessageResponse> deleteUser(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> requestBody
    ) {
        String password = requestBody.get("password");

        Integer userId = jwtService.extractUserId(token);


        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Vui lòng nhập mật khẩu để xác nhận xóa."));
        }

        try {
            userService.deleteSelfAccount(userId, password);
            return ResponseEntity.ok(new MessageResponse("Tài khoản đã được xóa thành công."));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Đã xảy ra lỗi trong quá trình xóa tài khoản."));
        }
    }


}
