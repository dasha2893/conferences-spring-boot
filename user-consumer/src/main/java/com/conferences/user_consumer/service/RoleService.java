package com.conferences.user_consumer.service;


import com.conferences.common.entity.Role;
import com.conferences.user_consumer.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.conferences.user_consumer.exception.NoSuchRoleException;

import java.util.Optional;

@Service
@Slf4j
@Transactional
public class RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    public Optional<Role> findByName(String name){
       return Optional.of(roleRepository.findByName(name)).orElseThrow(NoSuchRoleException::new);
    }
}
