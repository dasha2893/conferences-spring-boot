package com.conferences.spring.config;

import com.conferences.spring.service.kafka.DataSender;
import com.conferences.spring.service.kafka.KafkaDataSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.List;

@Configuration
@Slf4j
public class KafkaConfig {

    public final String usersTopic;
    public final String conferencesTopic;


    public KafkaConfig(@Value("${application.kafka.topics.users}") String usersTopic,
                       @Value("${application.kafka.topics.conferences}") String conferencesTopic){
        this.usersTopic = usersTopic;
        this.conferencesTopic = conferencesTopic;
    }

    @Bean
    public List<NewTopic> newTopics(){
        return List.of(
                TopicBuilder.name(usersTopic).partitions(1).replicas(1).build(),
                TopicBuilder.name(conferencesTopic).partitions(1).replicas(1).build()
        );
    }

    @Bean
    public ObjectMapper objectMapper(){
        return JacksonUtils.enhancedObjectMapper();
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory(KafkaProperties kafkaProperties, ObjectMapper mapper){
        var props = kafkaProperties.buildProducerProperties();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        var factory = new DefaultKafkaProducerFactory<String, Object>(props);
        factory.setValueSerializer(new JsonSerializer<>(mapper));
        return factory;
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Qualifier("userCreateSender")
    public DataSender userCreateSender(KafkaTemplate<String, Object> kafkaTemplate){
        return new KafkaDataSender(usersTopic, "create", kafkaTemplate, object -> log.info("User created: {}", object));
    }

    @Bean
    @Qualifier("userUpdateSender")
    public DataSender userUpdateSender(KafkaTemplate<String, Object> kafkaTemplate){
        return new KafkaDataSender(usersTopic, "update", kafkaTemplate, object -> log.info("User updated: {}", object));
    }

    @Bean
    @Qualifier("conferenceCreateSender")
    public DataSender conferenceCreateSender(KafkaTemplate<String, Object> kafkaTemplate){
        return new KafkaDataSender(conferencesTopic, "create", kafkaTemplate, object -> log.info("Conference created: {}", object));
    }

    @Bean
    @Qualifier("conferenceUpdateSender")
    public DataSender conferenceUpdateSender(KafkaTemplate<String, Object> kafkaTemplate){
        return new KafkaDataSender(conferencesTopic, "update", kafkaTemplate, object -> log.info("Conference updated: {}", object));
    }

    @Bean
    @Qualifier("conferenceDeleteSender")
    public DataSender conferenceDeleteSender(KafkaTemplate<String, Object> kafkaTemplate){
        return new KafkaDataSender(conferencesTopic, "delete", kafkaTemplate, object -> log.info("Conference deleted: {}", object));
    }
}
