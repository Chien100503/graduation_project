package com.petshop.petopia.controller;

import com.petshop.petopia.dto.request.auth.ForgetPasswordRequest;
import com.petshop.petopia.dto.request.auth.ResetPasswordRequest;
import com.petshop.petopia.service.auth.ForgetPasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PasswordResetController {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);

    @Autowired
    private ForgetPasswordService forgetPasswordService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> processForgotPasswordRequest(@RequestBody ForgetPasswordRequest req) {
        if (req == null || req.getEmail() == null || req.getEmail().trim().isEmpty()) {
            logger.warn("Password reset API request received with empty email.");
            return ResponseEntity.badRequest().body(Map.of("message", "Email không được để trống."));
        }

        try {
            forgetPasswordService.initiatePasswordReset(req.getEmail());
            logger.info("Password reset API request initiated for email: {}", req.getEmail());
            return ResponseEntity.ok(Map.of("message", "Nếu tài khoản của bạn tồn tại, chúng tôi đã gửi hướng dẫn đặt lại mật khẩu đến email của bạn."));
        } catch (Exception e) {
            logger.error("Error processing password reset API request for email: {}", req.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Đã xảy ra lỗi khi xử lý yêu cầu."));
        }
    }

    @PostMapping("/reset-password/{token}")
    public ResponseEntity<?> processResetPassword(
            @PathVariable("token") String token,
            @RequestBody ResetPasswordRequest req
    ) {
        logger.debug("Received password reset request with token: {}", token);

        if (req == null || req.getPassword() == null || req.getPassword().trim().isEmpty() ||
                req.getConfirmPassword() == null || req.getConfirmPassword().trim().isEmpty()) {
            logger.warn("Password reset API request received with missing password fields for token: {}", token);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Vui lòng nhập mật khẩu mới và xác nhận."));
        }

        if (!req.getPassword().equals(req.getConfirmPassword())) {
            logger.warn("Password reset API request received with non-matching passwords for token: {}", token);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Mật khẩu mới và xác nhận mật khẩu không khớp."));
        }

        boolean resetResult = forgetPasswordService.resetPassword(token, req.getPassword(), req.getConfirmPassword());

        if (resetResult) {
            logger.info("Password reset successful for token: {}", token);
            return ResponseEntity.ok(Map.of("success", true, "message", "Mật khẩu đã được đặt lại thành công."));
        } else {
            logger.warn("Password reset failed for token: {}", token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "Đường link đặt lại mật khẩu không hợp lệ hoặc đã hết hạn."));
        }
    }
}