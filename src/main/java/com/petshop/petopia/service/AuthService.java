package com.petshop.petopia.service;

import com.petshop.petopia.dto.request.auth.LoginRequest;
import com.petshop.petopia.dto.request.auth.RegisterRequest;
import com.petshop.petopia.dto.request.auth.VerifyRequest;
import com.petshop.petopia.dto.response.auth.LoginResponse;
import com.petshop.petopia.dto.response.auth.RegisterResponse;
import com.petshop.petopia.model.Role;
import com.petshop.petopia.model.User;
import com.petshop.petopia.model.VerificationCode;
import com.petshop.petopia.repository.user.RoleRepository;
import com.petshop.petopia.repository.user.UserRepository;
import com.petshop.petopia.repository.VerificationCodeRepository;
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
import java.util.Random;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final MailService mailService;
    private final VerificationCodeService verificationCodeService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final VerificationCodeRepository verificationCodeRepository;
    private final RoleRepository roleRepository;

    private final String VERIFICATION_SUBJECT = "Mã xác thực tài khoản";
    private final String VERIFICATION_TEXT_PREFIX = "Mã của bạn là: ";
    private final int VERIFICATION_CODE_TTL = 600; // 10 phút

    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(user.getUid(), token, user.getIsActive());
    }

    public RegisterResponse register(RegisterRequest req) {
        // Kiểm tra nếu email đã tồn tại
        Optional<User> existing = userRepository.findByEmail(req.getEmail());
        if (existing.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email đã tồn tại");
        }

        // Tạo mới đối tượng User
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setName(req.getName());
        user.setIsActive(false);  // Mới tạo thì chưa xác thực
        user.setCreatedAt(new Date());

        // Tìm hoặc tạo role "USER"
        Role userRole = roleRepository.findByName("USER").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("USER");
            return roleRepository.save(newRole);
        });

        // Gán role USER cho người dùng
        user.setRoles(new HashSet<>() {{
            add(userRole);
        }});

        // Lưu người dùng vào cơ sở dữ liệu
        userRepository.save(user);

        // Tạo mã xác thực 6 số
        String code = String.format("%06d", new Random().nextInt(999999));
        verificationCodeService.saveCode(user, code, VERIFICATION_CODE_TTL);

        // Gửi email xác thực
        mailService.sendMessage(user.getEmail(), VERIFICATION_SUBJECT,
                VERIFICATION_TEXT_PREFIX + code + " (hết hạn sau " + VERIFICATION_CODE_TTL / 60 + " phút)");

        // Tạo token cho người dùng
        String token = jwtService.generateToken(user.getEmail());

        // Trả về thông tin đăng ký, bao gồm UID, token và trạng thái isActive
        return new RegisterResponse(user.getUid(), token, user.getIsActive());
    }

    @Transactional
    public void verify(VerifyRequest req, String token) {
        String inputCode = req.getCode();

        // Lấy email từ token
        String email = jwtService.extractEmail(token);

        // Tìm user theo email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));

        // Lấy mã xác thực của người dùng
        VerificationCode vcode = verificationCodeRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã xác thực cho người dùng: " + user.getEmail()));

        // So sánh mã
        if (!vcode.getCode().equals(inputCode)) {
            throw new RuntimeException("Mã xác thực không chính xác");
        }

        // Cập nhật trạng thái và xóa mã xác thực
        user.setIsActive(true);
        userRepository.save(user);
        verificationCodeRepository.deleteByUser(user);
    }
}