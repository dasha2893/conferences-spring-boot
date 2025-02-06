package com.conferences.conf_consumer.service.kafka;


import com.conferences.common.entity.Conference;
import com.conferences.common.service.dto.ConferenceDTO;
import com.conferences.conf_consumer.service.ConferenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ConferenceKafkaListener {
    private final ConferenceService conferenceService;

    public ConferenceKafkaListener(ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    @KafkaListener(topics = "${application.kafka.topics.conferences}", containerFactory = "listenerContainerFactory")
    public void listenConferences(@Payload List<ConferenceDTO> conferences,
                                  @Header("action") String action,
                                  Acknowledgment acknowledgment) {

        try {
            conferences.forEach(conferenceDTO -> {
                log.info("Conference: {}", conferenceDTO.toString());
                switch (action) {
                    case "create":
                        conferenceService.createConference(conferenceDTO);
                        break;
                    case "update":
                        Conference conference = new Conference(conferenceDTO.getId(),
                                conferenceDTO.getTitle(),
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
                        conferenceService.editConference(conference);
                        break;
                    case "delete": conferenceService.deleteConference(conferenceDTO.getId());
                    default:
                        log.error("Unknown action: {}", action);
                }
            });
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error while listening to conferences: {}", e.getMessage());
        }
    }
}

