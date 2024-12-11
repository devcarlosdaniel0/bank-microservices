package com.project.auth.security.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.project.auth.security.exception.EmailNotFoundException;
import com.project.auth.security.exception.InvalidOrExpiredTokenException;
import com.project.core.repository.UserEntityRepository;
import com.project.auth.security.service.TokenService;
import com.project.core.domain.UserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final UserEntityRepository userRepository;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String token = this.recoverToken(request);
        if (token != null) {
            DecodedJWT decodedToken = tokenService.validateToken(token);
            String email = decodedToken.getSubject();
            Long userId = decodedToken.getClaim("userId").asLong();

            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EmailNotFoundException("Email not found"));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            authentication.setDetails(userId);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null) return null;

        return header.replace("Bearer ", "");
    }
}
