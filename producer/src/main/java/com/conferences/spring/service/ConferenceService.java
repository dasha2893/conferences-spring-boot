package com.conferences.spring.service;

import com.conferences.common.entity.Conference;
import com.conferences.common.service.dto.ConferenceDTO;
import com.conferences.spring.service.rest.ConferenceRestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ConferenceService {
    private final ConferenceRestService conferenceRestService;

    @Autowired
    public ConferenceService(ConferenceRestService conferenceRestService) {
        this.conferenceRestService = conferenceRestService;
    }

    public Optional<ConferenceDTO> getConferenceByTitle(String title, String userEmail) {
        try {
            return Optional.of(conferenceRestService.getConferenceByTitle(title, userEmail));
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<ConferenceDTO> getConferenceById(Long id, String userEmail) {
        try{
           return Optional.of(conferenceRestService.getConferenceById(id, userEmail));
        }catch (RuntimeException e){
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    public void deleteConference(Long id, String userEmail){
        conferenceRestService.deleteConference(id, userEmail);
    }

    public List<ConferenceDTO> getConferencesAfterYesterday(String userEmail){
        return conferenceRestService.getConferencesAfterYesterday(userEmail);
    }
}
