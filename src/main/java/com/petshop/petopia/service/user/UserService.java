package com.petshop.petopia.service.user;

import com.petshop.petopia.dto.request.user.UpdateProfileRequest;
import com.petshop.petopia.dto.response.auth.ProfileResponse;
import com.petshop.petopia.model.user.User;
import com.petshop.petopia.repository.user.UserRepository;
import com.petshop.petopia.service.FirebaseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final FirebaseService firebaseService;

    @Transactional
    public void deleteSelfAccount(Integer userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng với ID: " + userId));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Mật khẩu không chính xác.");
        }

        userRepository.delete(user);
    }

    public ProfileResponse profile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng với ID: " + userId));
        return new ProfileResponse(
                userId,
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getFirstName(),
                user.getLastName(),
                user.getAvatar()
        );
    }

    @Transactional
    public ProfileResponse updateProfile(Integer userId, UpdateProfileRequest request, MultipartFile avatarFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng với ID: " + userId));

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String oldAvatarUrl = user.getAvatar();
                String newAvatarUrl = firebaseService.uploadImageAvatar(avatarFile);
                user.setAvatar(newAvatarUrl);

                if (oldAvatarUrl != null && !oldAvatarUrl.isBlank()) {
                    firebaseService.deleteFileByUrl(oldAvatarUrl);
                }
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi xử lý ảnh đại diện", e);
            }
        }

        userRepository.save(user);

        return new ProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getFirstName(),
                user.getLastName(),
                user.getAvatar()
        );
    }

}
