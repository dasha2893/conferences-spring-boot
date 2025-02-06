package com.conferences.spring.service.kafka;


import com.conferences.common.service.dto.ConferenceDTO;
import com.conferences.common.service.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;


import java.util.function.Consumer;

@Slf4j
public class KafkaDataSender implements DataSender {

    private final String topic;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Consumer<Object> sendAsk;
    private final String action;


    public KafkaDataSender(String topic, String action, KafkaTemplate<String, Object> kafkaTemplate, Consumer<Object> sendAsk) {
        this.topic = topic;
        this.action = action;
        this.kafkaTemplate = kafkaTemplate;
        this.sendAsk = sendAsk;
    }

    @Override
    public void send(UserDTO userDTO) {
        kafkaTemplate.send(MessageBuilder.withPayload(userDTO)
                        .setHeader(KafkaHeaders.TOPIC, topic)
                        .setHeader("action", action)
                        .build())
                .addCallback(result -> {
                    log.info("User with email: {} was sent, offset: {}", userDTO.getEmail(), result.getRecordMetadata().offset());
                }, ex -> {
                    log.error("topic: {}, user: {}, exception: {}", topic, userDTO, ex.getMessage());
                });
    }

    @Override
    public void send(ConferenceDTO conferenceDTO) {
        kafkaTemplate.send(MessageBuilder.withPayload(conferenceDTO)
                        .setHeader(KafkaHeaders.TOPIC, topic)
                        .setHeader("action", action)
                        .build())
                .addCallback(result -> {
                    log.info("Conference id: {} was sent, offset: {}", conferenceDTO.getId(), result.getRecordMetadata().offset());
                }, ex -> {
                    log.error("topic: {}, conference: {}, exception: {}", topic, conferenceDTO, ex.getMessage());
                });
    }
}
