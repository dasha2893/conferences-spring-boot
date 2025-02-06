package com.conferences.spring.service.rest;


import com.conferences.common.service.dto.ConferenceDTO;
import com.conferences.common.service.dto.UserConferenceDTO;
import com.conferences.common.service.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserConferenceRestService {
    private final RestClientService restClientService;
    private final ObjectMapper objectMapper;
    @Value("${user-conf-consumer.url}")
    private String userConferenceUrl;

    @Autowired
    public UserConferenceRestService(RestClientService restClientService, ObjectMapper objectMapper) {
        this.restClientService = restClientService;
        this.objectMapper = objectMapper;
    }

    public List<UserConferenceDTO> getUserConferences(UserDTO userDTO) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", userDTO.getId());
        requestBody.put("email", userDTO.getEmail());

        return restClientService.makePostRequest(userConferenceUrl + "/getByUser",
                requestBody,
                new ParameterizedTypeReference<List<UserConferenceDTO>>(){},
                "No UserConferences for user: " + userDTO.getId());
    }



    public Optional<UserConferenceDTO> findUserConferenceByUserAndConference(UserDTO userDTO, ConferenceDTO conferenceDTO) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", userDTO.getId());
        requestBody.put("email", userDTO.getEmail());
        requestBody.put("conferenceId", conferenceDTO.getId());

        return Optional.of(restClientService.makePostRequest(userConferenceUrl + "/getByUserAndConference",
                requestBody,
                UserConferenceDTO.class,
                "No UserConferences for user: " + userDTO.getId()));

    }

}
