package com.conferences.spring.service;


import com.conferences.common.service.dto.AuthDTO;
import com.conferences.common.service.dto.UserDTO;
import com.conferences.spring.config.jwt.JwtTokenStorage;
import com.conferences.spring.exception.AuthenticationException;
import com.conferences.spring.service.rest.UserRestService;
import com.conferences.spring.config.jwt.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserService {

    private final UserRestService userRestService;
    private final JwtService jwtService;
    private final JwtTokenStorage jwtTokenStorage;

    @Autowired
    public UserService(UserRestService userRestService,
                       JwtService jwtService,
                       JwtTokenStorage jwtTokenStorage) {
        this.userRestService = userRestService;
        this.jwtService = jwtService;
        this.jwtTokenStorage = jwtTokenStorage;
    }

    public String authorizeUser(final String email,
                              final String password) {
        log.debug("authorizeUser: email: {}", email);
        String jwtToken = userRestService.login(new AuthDTO(email, password));
        if(jwtToken == null){
            throw new AuthenticationException();
        }

        List<String> userRoles = jwtService.extractRoles(jwtToken);
        if(userRoles.isEmpty()){
            throw new RuntimeException("User doesn't have any role");
        }

        log.debug("authorizeUser: jwtToken: {}, userRoles: {}", jwtToken, userRoles);
        jwtTokenStorage.saveToken(email, jwtToken);
        return jwtToken;
    }

    public void logout(String email){
        jwtTokenStorage.removeToken(email);
        SecurityContextHolder.clearContext();
    }

    public UserDTO getUserByEmail(String email){
        return userRestService.getUserByEmail(email);
    }
}