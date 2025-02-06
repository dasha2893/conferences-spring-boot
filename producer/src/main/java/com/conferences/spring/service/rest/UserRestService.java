package com.conferences.spring.service.rest;


import com.conferences.common.service.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserRestService {
    private final RestClientService restClientService;
    @Value("${user-consumer.url}")
    private String consumerServiceUrl;

    @Autowired
    public UserRestService(RestClientService restClientService) {
        this.restClientService = restClientService;
    }

    public UserDTO getUserByEmail(String email) {
        return restClientService.makeGetRequest(consumerServiceUrl + "/getByEmail/" + email, UserDTO.class, "No user found by email:" + email);
    }
}
