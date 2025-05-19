package com.petshop.petopia.service;

import com.petshop.petopia.model.user.User;
import com.petshop.petopia.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@AllArgsConstructor
public class ForgetPasswordService {

    private static final Logger logger = LoggerFactory.getLogger(ForgetPasswordService.class);

    private UserRepository userRepository;

    private RedisTemplate<String, String> redisTemplate; // Sử dụng RedisTemplate cho String key/value

    private JavaMailSender mailSender;

    private PasswordEncoder passwordEncoder;

    @Value("${app.host.url}")
    private String appHostUrl;

    private static final String PASSWORD_RESET_TOKEN_PREFIX = "passwordreset:";
    private static final Duration TOKEN_EXPIRATION = Duration.ofMinutes(30); // Thời gian sống 30 phút



    @Transactional
    public void initiatePasswordReset(String userEmail) {
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = UUID.randomUUID().toString();
            String redisKey = PASSWORD_RESET_TOKEN_PREFIX + token;
            redisTemplate.opsForValue().set(redisKey, String.valueOf(user.getId()), TOKEN_EXPIRATION);
            sendPasswordResetEmail(user, token);
            logger.info("Password reset initiated for user: {}", userEmail);
        } else {
            logger.warn("Password reset requested for non-existent email: {}", userEmail);
            throw new IllegalArgumentException("Không tìm thấy người dùng với email này."); // Báo lỗi cho controller
        }
    }

    private void sendPasswordResetEmail(User user, String token) {
        String resetUrl = appHostUrl + "/reset-password/" + token;
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("Yêu cầu đặt lại mật khẩu Petopia");
        email.setText("Xin chào " + user.getEmail() + ",\n\n"
                + "Bạn nhận được email này vì bạn (hoặc ai đó) đã yêu cầu đặt lại mật khẩu cho tài khoản của bạn.\n"
                + "Vui lòng click vào đường link sau để hoàn tất quá trình:\n"
                + resetUrl + "\n\n"
                + "Đường link này sẽ hết hạn sau " + TOKEN_EXPIRATION.toMinutes() + " phút.\n"
                + "Nếu bạn không yêu cầu việc này, vui lòng bỏ qua email này.\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ Petopia");
        try {
            mailSender.send(email);
        } catch (Exception e) {
            logger.error("Failed to send password reset email to: {}", user.getEmail(), e);
            throw new RuntimeException("Lỗi khi gửi email đặt lại mật khẩu.", e); // Báo lỗi cho controller
        }
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword, String confirmPassword) {
        if (isTokenInvalid(token)) {
            logger.warn("Password reset attempt with invalid token.");
            return false;
        }
        if (isPasswordInvalid(newPassword, confirmPassword)) {
            logger.warn("Invalid new password or confirm password for token: {}", token);
            return false;
        }

        String redisKey = PASSWORD_RESET_TOKEN_PREFIX + token;
        String userIdString = redisTemplate.opsForValue().get(redisKey);

        if (userIdString != null) {
            try {
                Integer userId = Integer.parseInt(userIdString);
                Optional<User> userOptional = userRepository.findById(userId);

                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    String encodedPassword = passwordEncoder.encode(newPassword);
                    user.setPassword(encodedPassword);
                    userRepository.save(user);
                    redisTemplate.delete(redisKey);
                    logger.info("Password successfully reset for user: {}", user.getEmail());
                    return true;
                } else {
                    logger.warn("User not found for token: {}", token);
                    return false;
                }
            } catch (NumberFormatException e) {
                logger.error("Invalid User ID format for token {}: {}", token, userIdString, e);
                redisTemplate.delete(redisKey);
                return false;
            }
        } else {
            logger.warn("Token not found or expired: {}", token);
            return false;
        }
    }

    private boolean isTokenInvalid(String token) {
        return token == null || token.trim().isEmpty();
    }

    private boolean isPasswordInvalid(String newPassword, String confirmPassword) {
        return newPassword == null || newPassword.trim().isEmpty() || confirmPassword == null || confirmPassword.trim().isEmpty() || !newPassword.equals(confirmPassword);
    }

    @Transactional(readOnly = true)
    public boolean isResetTokenValid(String token) {
        if (isTokenInvalid(token)) {
            logger.debug("Validation requested for null or empty token.");
            return false;
        }
        String redisKey = PASSWORD_RESET_TOKEN_PREFIX + token;
        return redisTemplate.hasKey(redisKey);
    }
}