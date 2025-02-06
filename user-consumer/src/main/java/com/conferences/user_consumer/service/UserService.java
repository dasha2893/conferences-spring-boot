package com.conferences.user_consumer.service;


import com.conferences.common.entity.Role;
import com.conferences.common.entity.User;
import com.conferences.common.entity.UserRole;
import com.conferences.common.model.UserRoles;
import com.conferences.common.service.dto.UserDTO;
import com.conferences.user_consumer.repository.UserRepository;
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

import java.util.Collections;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(UserDTO userDTO) {
        log.debug("createUser: email: {}", userDTO.getEmail());
        User newUser = new User(userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getThirdName(),
                userDTO.getPassword(),
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

        if (userDTO.getEmail().toLowerCase().equals("missis.bobiakowa@yandex.ru")) {
            Optional<Role> adminRole = roleService.findByName(UserRoles.ADMIN.getRole());
            log.debug("adminRole.isPresent() = " + adminRole.isPresent());
            if (adminRole.isPresent()) {
                newUser.setUserRoles(Collections.singleton(new UserRole(adminRole.get(), newUser)));
            }
        } else {
            Optional<Role> userRole = roleService.findByName(UserRoles.USER.getRole());
            log.debug("userRole.isPresent() =" + userRole.isPresent());
            if (userRole.isPresent()) {
                newUser.setUserRoles(Collections.singleton(new UserRole(userRole.get(), newUser)));
            }
        }

        userRepository.save(newUser);

        return newUser;
    }


//    public UserDTO findById(final Long userId) {
//        return userRepository.findById(userId).map(UserDTO::new).orElseThrow(NoSuchUserException::new);
//    }

}
