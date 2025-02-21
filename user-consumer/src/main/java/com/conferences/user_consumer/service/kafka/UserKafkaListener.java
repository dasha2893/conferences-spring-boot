package com.conferences.user_consumer.service.kafka;



import com.conferences.common.entity.Conference;
import com.conferences.common.service.dto.ConferenceDTO;
import com.conferences.common.service.dto.UserDTO;
import com.conferences.user_consumer.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserKafkaListener {
    private final UserService userService;

    @Autowired
    public UserKafkaListener(UserService userService){
        this.userService = userService;
    }

    @KafkaListener(topics = "${application.kafka.topics.users}", containerFactory = "listenerContainerFactory")
    public void listenUsers(@Payload List<UserDTO> users, @Headers Map<String, Object> headers, Acknowledgment acknowledgment) {

        if (headers.containsKey("kafka_batchConvertedHeaders")) {
            Map<String, Object> batchHeaders = (Map<String, Object>) headers.get("kafka_batchConvertedHeaders");
            if (batchHeaders != null && batchHeaders.containsKey("action")) {
                String action = batchHeaders.get("action").toString();
                log.info("Extracted action: {}", action);

                processUsers(users, action, acknowledgment);
            } else {
                log.error("Missing 'action' in kafka_batchConvertedHeaders");
            }
        } else {
            log.error("Missing 'kafka_batchConvertedHeaders' in headers");
        }
    }


    private void processUsers(List<UserDTO> users, String action, Acknowledgment acknowledgment) {
        try {
            users.forEach(userDTO -> {
                log.info("User: {}", userDTO.toString());
                switch (action){
                    case "create":
                        userService.createUser(userDTO);
                    default: log.error("Unknown action: {}", action);
                }
            });
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error while listening to users: {}", e.getMessage());
        }
    }
}

