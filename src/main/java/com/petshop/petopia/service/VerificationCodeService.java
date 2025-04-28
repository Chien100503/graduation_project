package com.petshop.petopia.service;

import com.petshop.petopia.model.user.User;
import com.petshop.petopia.repository.user.UserRepository;
import com.petshop.petopia.model.VerificationCode;
import com.petshop.petopia.repository.verify.VerificationCodeRepository;
import com.petshop.petopia.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final JwtService jwtService;

    private final String VERIFICATION_SUBJECT = "Mã xác thực";
    private final String VERIFICATION_TEXT_PREFIX = "Mã xác thực của bạn là: ";

    public void resendCode(String token) {
        String email = jwtService.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));

        String currentCode = getCode(user);

        if (currentCode != null) {
            mailService.sendMessage(user.getEmail(), VERIFICATION_SUBJECT, VERIFICATION_TEXT_PREFIX + currentCode);
            System.out.println("Đã gửi lại mã xác thực cũ cho email: " + user.getEmail());
        } else {
            String newCode = generateVerificationCode();
            saveCode(user, newCode, 300);
            mailService.sendMessage(user.getEmail(), VERIFICATION_SUBJECT, VERIFICATION_TEXT_PREFIX + newCode);
            System.out.println("Đã tạo và gửi mã xác thực mới cho email: " + user.getEmail());
        }
    }

    public void saveCode(User user, String code, int ttlSeconds) {
        long expiryTime = System.currentTimeMillis() + ttlSeconds * 1000L;

        verificationCodeRepository.deleteByUser(user);

        VerificationCode vc = new VerificationCode();
        vc.setCode(code);
        vc.setExpiryTime(expiryTime);
        vc.setUser(user);

        verificationCodeRepository.save(vc);

        mailService.sendMessage(user.getEmail(), VERIFICATION_SUBJECT, VERIFICATION_TEXT_PREFIX + code);
    }

    public String getCode(User user) {
        Optional<VerificationCode> optional = verificationCodeRepository.findByUser_Id(user.getId());
        if (optional.isEmpty()) return null;

        VerificationCode vc = optional.get();
        if (System.currentTimeMillis() > vc.getExpiryTime()) {
            verificationCodeRepository.delete(vc);
            return null;
        }

        return vc.getCode();
    }

    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 1000000));
    }

    @Transactional
    public void cleanUp() {
        long now = System.currentTimeMillis();
        verificationCodeRepository.deleteAllByExpiryTimeLessThan(now);
    }
}