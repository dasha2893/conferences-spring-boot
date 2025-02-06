package com.conferences.spring.service;

import com.conferences.common.entity.User;
import com.conferences.common.service.dto.UserDTO;
import com.conferences.spring.exception.AuthenticationException;
import com.conferences.spring.service.rest.UserRestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@Slf4j
public class UserService implements UserDetailsService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRestService userRestService;

    @Autowired
    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder,
                       UserRestService userRestService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRestService = userRestService;
    }

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("loadUserByUsername: email: {}", email);

        try {
            UserDTO userDTO = userRestService.getUserByEmail(email);
            return new User(userDTO.getFirstName(),
                            userDTO.getLastName(),
                            userDTO.getThirdName(),
                            userDTO.getPassword(),
                            userDTO.getDateOfBirth(),
                            userDTO.getGender(),
                            userDTO.getEmail());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new UsernameNotFoundException("User not found: " + email, e);
        }
    }

    public UserDetails authorizeUser(final String email,
                                     final String password) {
        log.debug("authorizeUser: email: {}", email);
        UserDetails loadedUser = loadUserByUsername(email);

        if (!bCryptPasswordEncoder.matches(password, loadedUser.getPassword())) {
            throw new AuthenticationException();
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loadedUser, password, loadedUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        return loadedUser;
    }

    public UserDTO encodePassword(UserDTO userDTO){
        String encodedPass = bCryptPasswordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(encodedPass);
        return userDTO;
    }

}