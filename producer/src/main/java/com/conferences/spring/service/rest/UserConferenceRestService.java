package com.conferences.spring.service.rest;


import com.conferences.common.service.dto.ConferenceDTO;
import com.conferences.common.service.dto.UserConferenceDTO;
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

    public List<UserConferenceDTO> getUserConferences(String userEmail) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userEmail", userEmail);

        Map<String, List<UserConferenceDTO>> response = restClientService.makePostRequest(userConferenceUrl + "/getByUser",
                requestBody,
                new ParameterizedTypeReference<Map<String, List<UserConferenceDTO>>>() {
                },
                "No UserConferences for user: " + userEmail,
                userEmail);

        if(response.containsKey("conferences")){
            return response.get("conferences");
        }

        return List.of();
    }



    public Optional<UserConferenceDTO> findUserConferenceByUserAndConference(ConferenceDTO conferenceDTO, String userEmail) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", userEmail);
        requestBody.put("conferenceId", conferenceDTO.getId());

        return Optional.ofNullable(restClientService.makePostRequest(userConferenceUrl + "/getByUserAndConference",
                requestBody,
                UserConferenceDTO.class,
                "No UserConferences for user: " + userEmail, userEmail));

    }

}
