package com.conferences.user_consumer.service;


import com.conferences.common.entity.Role;
import com.conferences.common.entity.User;
import com.conferences.common.entity.UserRole;
import com.conferences.common.model.UserRoles;
import com.conferences.common.service.dto.AuthDTO;
import com.conferences.common.service.dto.UserDTO;
import com.conferences.user_consumer.repository.UserRepository;
import com.conferences.user_consumer.service.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.conferences.user_consumer.exception.NoSuchUserException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleService roleService,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("loadUserByUsername: email: {}", email);

        Optional<User> user = findByEmail(email);
        return user.orElseThrow(NoSuchUserException::new);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public String handleLogin(AuthDTO authDTO){
        log.info("handleLogin: authDTO: {}", authDTO);
        Optional<User> userOpt = findByEmail(authDTO.getEmail());

        if(userOpt.isPresent() && bCryptPasswordEncoder.matches(authDTO.getPassword(), userOpt.get().getPassword())){

            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword(), userOpt.get().getAuthorities()));

            List<String> rolesList = userOpt.get().getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            String jwtToken = jwtService.generateToken(authDTO.getEmail(), rolesList);
            log.info("handleLogin: jwtToken: {}", jwtToken);
            return jwtToken;
        }
        return null;
    }

    public User createUser(UserDTO userDTO) {
        log.debug("createUser: email: {}", userDTO.getEmail());
        String encodedPassword = bCryptPasswordEncoder.encode(userDTO.getPassword());

        User newUser = new User(userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getThirdName(),
                encodedPassword,
                userDTO.getDateOfBirth(),
                userDTO.getGender(),
                userDTO.getEmail());

        newUser.setPhone(userDTO.getPhone());
        newUser.setOrganization(userDTO.getOrganization());
        newUser.setUniversity(userDTO.getUniversity());
        newUser.setDepartment(userDTO.getDepartment());
        newUser.setEducationType(userDTO.getEducationType());
        newUser.setEducationStatus(userDTO.getEducationStatus());
        newUser.setGraduationYear(userDTO.getGraduationYear());

        if (userDTO.getEmail().toLowerCase().equals("missis@yandex.ru")) {
            Optional<Role> adminRole = roleService.findByName(UserRoles.ADMIN.getRole());
            if (adminRole.isPresent()) {
                newUser.setUserRoles(Collections.singleton(new UserRole(adminRole.get(), newUser)));
            }
        } else {
            Optional<Role> userRole = roleService.findByName(UserRoles.USER.getRole());
            if (userRole.isPresent()) {
                newUser.setUserRoles(Collections.singleton(new UserRole(userRole.get(), newUser)));
            }
        }

        log.debug("createUser: newUser: {}", newUser);

        userRepository.save(newUser);

        return newUser;
    }


}
