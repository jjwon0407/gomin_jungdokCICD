package com.gomin_jungdok.gdgoc;

import com.gomin_jungdok.gdgoc.jwt.JwtUtil;
import com.gomin_jungdok.gdgoc.user.User;
import com.gomin_jungdok.gdgoc.user.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class TokenFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final JwtUtil jwtTokenProvider;

    public TokenFilter(UserRepository userRepository, JwtUtil jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();  // 현재 요청 경로 확인
        String token = resolveToken(request);


        // ✅ `/refresh` 경로는 필터링 제외 (Refresh Token 검증은 AuthController에서 수행)
        if (requestURI.equals("/api/auth/kakao/refresh")) {
            System.out.println("/api/auth/kakao/refresh");
            System.out.println("token = " + token);
            filterChain.doFilter(request, response);
            return;
        }

        if (token != null) {
            try {
                if (isFirebaseToken(token)) {
                    System.out.println("not firebase");
                    validateFirebaseToken(token);
                } else {
                    System.out.println("it's jwt token");
                    validateJwtToken(token);
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized: Invalid Token");
                return;
            }

        }
        filterChain.doFilter(request, response);
    }

    private boolean isFirebaseToken(String token) {
        try {
            FirebaseAuth.getInstance().verifyIdToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void validateFirebaseToken(String token) throws Exception {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
        String uid = decodedToken.getUid();
        Long id = Long.parseLong(uid);
        System.out.println("id = " + id);

        Optional<User> user = userRepository.findById(id);   //수정 필요 string int

        if (user.isEmpty() || (!"GOOGLE".equals(user.get().getSocialType()) && !"APPLE".equals(user.get().getSocialType()))) {
            throw new Exception("Invalid GOOGLE/APPLE user");
        }
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(uid, null, null));
    }


    private void validateJwtToken(String token) throws Exception {

        if(jwtTokenProvider.validateAndGetUserId(token) == null) {
            System.out.println("Invalid JWT token");
            throw new Exception("Invalid JWT token");
        }

        try {
            String userId = jwtTokenProvider.validateAndGetUserId(token);
            System.out.println("userId = " + userId);

            Optional<User> user = userRepository.findById(Long.parseLong(userId));
            if (user.isEmpty()) {
                System.out.println("Invalid JWT token");
                throw new Exception("Invalid JWT token");
            }
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(userId, null, null)
            );
        } catch (Exception e) {
            System.out.println("Error in validateJwtToken: " + e.getMessage());
            throw new Exception("Invalid JWT token");
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}


