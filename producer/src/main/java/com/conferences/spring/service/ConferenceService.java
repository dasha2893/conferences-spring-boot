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

    public Optional<ConferenceDTO> getConferenceByTitle(String title) {
        try {
            return Optional.of(conferenceRestService.getConferenceByTitle(title));
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<ConferenceDTO> getConferenceById(Long id) {
        try{
           return Optional.of(conferenceRestService.getConferenceById(id));
        }catch (RuntimeException e){
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    public void deleteConference(Long id){
        conferenceRestService.deleteConference(id);
    }

    public List<ConferenceDTO> getConferencesAfterYesterday(){
        return conferenceRestService.getConferencesAfterYesterday();
    }
}
