package com.petshop.petopia.service;

import com.petshop.petopia.model.user.User;
import com.petshop.petopia.repository.user.UserRepository;
import com.petshop.petopia.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class VerificationCodeService {

    private final StringRedisTemplate redisTemplate;

    private final UserRepository userRepository;
    private final MailService mailService;
    private final JwtService jwtService;

    private final String VERIFICATION_SUBJECT = "Mã xác thực";
    private final String VERIFICATION_TEXT_PREFIX = "Mã xác thực của bạn là: ";
    private final String VERIFICATION_CODE_PREFIX = "verify_code:"; // key prefix
    private final int VERIFICATION_CODE_TTL_SECONDS = 600;

    public void resendCode(String token) {
        String email = jwtService.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));

        String currentCode = getCode(user);

        if (currentCode != null) {
            mailService.sendMessage(user.getEmail(), VERIFICATION_SUBJECT, VERIFICATION_TEXT_PREFIX + currentCode);
            System.out.println("Đã gửi lại mã xác thực cũ cho email: " + email);
        } else {
            String newCode = generateVerificationCode();
            saveCode(user, newCode);
            mailService.sendMessage(user.getEmail(), VERIFICATION_SUBJECT, VERIFICATION_TEXT_PREFIX + newCode);
        }
    }



    public void saveCode(User user, String code) {
        String key = VERIFICATION_CODE_PREFIX + user.getId();
        redisTemplate.opsForValue().set(key, code, VERIFICATION_CODE_TTL_SECONDS, TimeUnit.SECONDS);
    }

    public String getCode(User user) {
        String key = VERIFICATION_CODE_PREFIX + user.getId();
        return redisTemplate.opsForValue().get(key);
    }

    public String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    public void deleteCode(User user) {
        String key = VERIFICATION_CODE_PREFIX + user.getId();
        redisTemplate.delete(key);
    }
}