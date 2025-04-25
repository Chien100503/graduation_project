package com.petshop.petopia.security;

import com.petshop.petopia.implement.UserDetailsServiceImpl;
import com.petshop.petopia.model.user.User;
import com.petshop.petopia.repository.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        // Chỉ bỏ qua login và register khỏi việc kiểm tra token
        if (path.startsWith("/api/login") || path.startsWith("/api/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        // Kiểm tra Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Lấy token
            email = jwtService.extractEmail(token); // Lấy email từ token
        }

        // Kiểm tra token và email có hợp lệ và chưa có authentication trong SecurityContext
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Kiểm tra tính hợp lệ của token
            if (jwtService.validateToken(token, userDetails)) {
                // Lấy người dùng từ DB để kiểm tra trạng thái isActive
                User user = userRepository.findByEmail(email).orElse(null);

                // Kiểm tra isActive cho các endpoint khác /api/verify và /api/resend
                if (user != null && !user.getIsActive() && !path.startsWith("/api/verify") && !path.startsWith("/api/resend")) {
                    // Trả về JSON nếu tài khoản chưa được xác thực
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("""
                        {
                            "error": "AccountNotActive",
                            "message": "Tài khoản của bạn chưa được xác thực.",
                            "nextStep": "/api/verify"
                        }
                    """);
                    return;
                }

                // Tạo đối tượng Authentication và gán vào SecurityContext
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response); // Tiếp tục chuỗi lọc
    }
}
