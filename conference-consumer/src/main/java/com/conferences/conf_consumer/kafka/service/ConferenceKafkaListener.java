package com.conferences.conf_consumer.kafka.service;


import com.conferences.common.entity.Conference;
import com.conferences.common.service.dto.ConferenceDTO;
import com.conferences.conf_consumer.service.ConferenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ConferenceKafkaListener {
    private final ConferenceService conferenceService;

    public ConferenceKafkaListener(ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    @KafkaListener(topics = "${application.kafka.topics.conferences}",
            containerFactory = "listenerContainerFactory")
    public void listenConferences(@Payload List<ConferenceDTO> conferences,
                                  @Headers Map<String, Object> headers,
                                  Acknowledgment acknowledgment) {
        log.info("Received conferences from {}", conferences);
        log.info("headers: {}", headers);

        try {
            if (headers.containsKey("kafka_batchConvertedHeaders")) {
                List<Map<String, Object>> batchHeaders = (List<Map<String, Object>>) headers.get("kafka_batchConvertedHeaders");
                for (int i = 0; i < conferences.size(); i++) {
                    ConferenceDTO conferenceDTO = conferences.get(i);
                    Map<String, Object> individualHeaders = batchHeaders.get(i);

                    String action = individualHeaders.get("action").toString();
                    processConference(conferenceDTO, action);
                }
                acknowledgment.acknowledge();
            } else {
                log.error("Missing 'kafka_batchConvertedHeaders' in headers");
            }
        }catch (Exception e){
            log.error("Error processing conferences", e);
        }

    }

    private void processConference(ConferenceDTO conferenceDTO, String action) {
        try {
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
                    case "delete":
                        conferenceService.deleteConference(conferenceDTO.getId());
                    default:
                        log.error("Unknown action: {}", action);
                }
        } catch (Exception e) {
            throw new RuntimeException("Error while listening to conferences: " + e.getMessage());
        }
    }
}

