package com.petshop.petopia.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.petopia.implement.UserDetailsServiceImpl;
import com.petshop.petopia.model.user.User;
import com.petshop.petopia.repository.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.startsWith("/api/login") || path.startsWith("/api/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String email = null;
            UserDetails userDetails = null;
            User user = null;

            try {
                email = jwtService.extractEmail(token);

                if (email != null) {
                    userDetails = userDetailsService.loadUserByUsername(email);

                    if (jwtService.validateToken(token, userDetails)) {
                        user = userRepository.findByEmail(email).orElse(null);

                        if (user != null) {
                            if (!user.getIsActive() && !path.startsWith("/api/verify") && !path.startsWith("/api/resend")) {
                                logger.warn("Attempted access by inactive user: {}", email);
                                handleInactiveAccount(response);
                                return;
                            }

                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                            logger.debug("Authentication successful for user: {}", email);
                        } else {
                            logger.error("User entity not found in DB for email extracted from valid token: {}", email);
                            handleInvalidToken(response, "User associated with token not found.");
                            return;
                        }
                    } else {
                        handleInvalidToken(response, "Token không hợp lệ."); // Trả về lỗi token không hợp lệ
                        return;
                    }
                } else {
                    handleInvalidToken(response, "Token không hợp lệ hoặc đã hết hạn."); // Trả về lỗi token không hợp lệ
                    return;
                }
            } catch (Exception e) {
                logger.error("Unexpected error during JWT filter processing", e);
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.getWriter().write("An unexpected error occurred."); // Hoặc JSON lỗi chi tiết hơn
                return; // Dừng xử lý filter chain
            }
        }

        filterChain.doFilter(request, response);
    }

    private void handleInvalidToken(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "InvalidToken");
        errorResponse.put("message", message);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        logger.debug("Responded with InvalidToken: {}", message);
    }

    private void handleInactiveAccount(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value()); // Sử dụng 403 Forbidden
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "AccountNotActive");
        errorResponse.put("message", "Tài khoản của bạn chưa được xác thực.");
        errorResponse.put("isActive", false);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        logger.warn("Responded with AccountNotActive error.");
    }
}
