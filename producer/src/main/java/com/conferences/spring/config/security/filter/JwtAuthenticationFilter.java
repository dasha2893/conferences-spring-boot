package com.conferences.spring.config.security.filter;

import com.conferences.spring.config.jwt.JwtService;
import com.conferences.spring.config.jwt.JwtTokenStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final JwtTokenStorage jwtTokenStorage;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService,
                                   JwtTokenStorage jwtTokenStorage) {
        this.jwtService = jwtService;
        this.jwtTokenStorage = jwtTokenStorage;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = null;
        String authHeader = request.getHeader("Authorization");

        log.info("authHeader: {}", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token == null) {
            Cookie[] cookies = request.getCookies();
            if(cookies != null){
                for (Cookie cookie : cookies) {
                    if ("JWT".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String email = jwtService.extractEmail(token);

            log.info("email: {}", email);
            log.debug("token: {}", token);

            if (!jwtTokenStorage.isTokenValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (email != null && jwtService.validateToken(token)) {
                jwtTokenStorage.saveToken(email, token);

                List<String> userRoles = jwtService.extractRoles(token);
                List<SimpleGrantedAuthority> roles = userRoles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

                UserDetails user = new User(email, "", roles);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(user, token, roles);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
