package com.conferences.spring.service.rest;

import com.conferences.common.service.dto.ConferenceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ConferenceRestService {
    private final RestClientService restClientService;

    @Value("${conference-consumer.url}")
    private String conferenceUrl;


    @Autowired
    public ConferenceRestService(RestClientService restClientService) {
        this.restClientService = restClientService;
    }

    public ConferenceDTO getConferenceByTitle(String title, String userEmail) {
        return restClientService.makeGetRequest(conferenceUrl + "/getByTitle/" + title, ConferenceDTO.class, "Conference Not Found:" + title, userEmail);
    }

    public ConferenceDTO getConferenceById(Long id, String userEmail) {
        return restClientService.makePostRequest(conferenceUrl + "/getById", id, ConferenceDTO.class, "Conference Not Found:" + id, userEmail);
    }

    public void deleteConference(Long id, String userEmail) {
        restClientService.makeDeleteRequest(conferenceUrl + "/delete/" + id, "Conference Not Found:" + id, userEmail);
    }

    public List<ConferenceDTO> getConferencesAfterYesterday(String userEmail){
        return restClientService.makeGetRequest(conferenceUrl + "/getAll", new ParameterizedTypeReference<List<ConferenceDTO>>() {}, "Conferences Not Found", userEmail);
    }

}
