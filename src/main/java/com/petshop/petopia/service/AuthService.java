package com.petshop.petopia.service;

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
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

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
}