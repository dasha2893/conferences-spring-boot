package com.conferences.conf_consumer.service;

import com.conferences.common.entity.Conference;
import com.conferences.common.service.dto.ConferenceDTO;
import com.conferences.conf_consumer.repository.ConferenceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class ConferenceService {
    private final ConferenceRepository conferenceRepository;

    @Autowired
    public ConferenceService(ConferenceRepository conferenceRepository){
        this.conferenceRepository = conferenceRepository;
    }

    public Optional<Conference> getConferenceByTitle(String title){
        return conferenceRepository.findByTitle(title);
    }

    public Conference createConference(ConferenceDTO conferenceDTO){
        Conference conference = new Conference(conferenceDTO.getTitle(),
                conferenceDTO.getShortDescription(),
                conferenceDTO.getFullDescription(),
                conferenceDTO.getLocation(),
                conferenceDTO.getOrganizer(),
                conferenceDTO.getContacts(),
                conferenceDTO.getStartDate(),
                conferenceDTO.getEndDate(),
                conferenceDTO.getStartRegistration(),
                conferenceDTO.getEndRegistration(),
                conferenceDTO.getCreatedBy());

        return conferenceRepository.save(conference);
    }

    public void deleteConference(Long conferenceId){
        conferenceRepository.deleteById(conferenceId);
    }

    @Transactional(readOnly = true)
    public Optional<Conference> getConferenceById(Long conferenceId){
        return conferenceRepository.findById(conferenceId);
    }

    public Conference editConference(Conference conference){
        return conference;
    }

    @Transactional(readOnly = true)
    public List<Conference> getConferencesAfterYesterday(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date yesterday = calendar.getTime();

        return conferenceRepository.findByStartDateGreaterThan(yesterday);
    }
}
