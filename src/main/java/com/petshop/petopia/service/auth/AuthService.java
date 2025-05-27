package com.petshop.petopia.service.auth;

import com.petshop.petopia.dto.request.auth.LoginRequest;
import com.petshop.petopia.dto.request.auth.RegisterRequest;
import com.petshop.petopia.dto.request.auth.VerifyRequest;
import com.petshop.petopia.dto.response.auth.LoginResponse;
import com.petshop.petopia.dto.response.auth.RegisterResponse;
import com.petshop.petopia.model.user.Role;
import com.petshop.petopia.model.user.User;
import com.petshop.petopia.repository.user.RoleRepository;
import com.petshop.petopia.repository.user.UserRepository;
import com.petshop.petopia.security.JwtService;
import com.petshop.petopia.service.MailService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final MailService mailService;
    private final VerificationCodeService verificationCodeService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final RoleRepository roleRepository;


    private final String VERIFICATION_SUBJECT = "Mã xác thực tài khoản";
    private final String VERIFICATION_TEXT_PREFIX = "Mã của bạn là: ";
    private final int VERIFICATION_CODE_TTL = 600;

    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(user.getId(), token, user.getIsActive());
    }

    public void logout(String token) {
        jwtService.invalidateToken(token);
    }

    public RegisterResponse register(RegisterRequest req) {
        Optional<User> existing = userRepository.findByEmail(req.getEmail());
        if (existing.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email đã tồn tại");
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setPhone(req.getPhone());
//        user.setAddress(req.getAddress());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setName(req.getName());
        user.setIsActive(false);
        user.setCreatedAt(new Date());

        Role userRole = roleRepository.findByName("USER").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("USER");
            return roleRepository.save(newRole);
        });

        user.setRoles(new HashSet<>() {{
            add(userRole);
        }});

        userRepository.save(user);

        String code = verificationCodeService.generateVerificationCode();
        verificationCodeService.saveCode(user, code);

        mailService.sendMessage(user.getEmail(), VERIFICATION_SUBJECT,
                VERIFICATION_TEXT_PREFIX + code + " (hết hạn sau " + VERIFICATION_CODE_TTL / 60 + " phút)");

        String token = jwtService.generateToken(user.getEmail());

        return new RegisterResponse(user.getId(), token, user.getIsActive());
    }

    @Transactional
    public void verify(VerifyRequest req, String token) {
        String inputCode = req.getCode();

        String email = jwtService.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));

        String savedCode = verificationCodeService.getCode(user);

        if (savedCode == null) {
            throw new RuntimeException("Mã xác thực đã hết hạn hoặc không tồn tại");
        }

        if (!savedCode.equals(inputCode)) {
            throw new RuntimeException("Mã xác thực không chính xác");
        }

        user.setIsActive(true);
        userRepository.save(user);
        verificationCodeService.deleteCode(user);
    }

    @Service
    @RequiredArgsConstructor
    public static class VerificationCodeService {

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
}