package com.petshop.petopia.implement;

import com.petshop.petopia.model.Role;
import com.petshop.petopia.model.User;
import com.petshop.petopia.repository.user.RoleRepository;
import com.petshop.petopia.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Lấy thông tin người dùng từ email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

        // Lấy danh sách roles từ repository
        List<Role> roles = roleRepository.findRolesByEmail(email);
        if (roles.isEmpty()) {
            System.out.println("No roles found for user: " + email);
        }

        // Chuyển đổi danh sách roles thành quyền có tiền tố "ROLE_"
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());

        System.out.println("Granted Authorities: " + authorities);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                true, // account non-expired
                true, // credentials non-expired
                true, // account enabled
                true, // account not locked
                authorities
        );
    }
}
