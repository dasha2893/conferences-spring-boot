package com.conferences.spring.service.rest;


import com.conferences.common.service.dto.AuthDTO;
import com.conferences.common.service.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import java.util.Map;


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

    public String login(AuthDTO authDTO) {
        Map<String, String> response = restClientService.makePostRequest(consumerServiceUrl + "/login",
                authDTO,
                new ParameterizedTypeReference<Map<String, String>>() {
                },
                "No user found by email:" + authDTO.getEmail(), authDTO.getEmail());

        log.info("login: response: {}" + response);

        if(response.containsKey("token")) {
            return response.get("token");
        }

        return null;
    }

    public UserDTO getUserByEmail(String email) {
        return restClientService.makePostRequest(consumerServiceUrl + "/getByEmail", email, UserDTO.class, "No user found by email:" + email, email);
    }
}
