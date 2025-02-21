package com.conferences.user_consumer.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;


@Configuration
@Slf4j
public class KafkaConfig {
    public final String usersTopic;

    public KafkaConfig(@Value("${application.kafka.topics.users}") String usersTopic) {
        this.usersTopic = usersTopic;
    }


    @Bean
    public ObjectMapper objectMapper() {
        return JacksonUtils.enhancedObjectMapper();
    }


    @Bean
    public ConsumerFactory<String, Object> consumerFactory(KafkaProperties kafkaProperties, ObjectMapper mapper) {
        var props = kafkaProperties.buildConsumerProperties();

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        JsonDeserializer<Object> deserializer = new JsonDeserializer<>(Object.class, mapper);
        deserializer.addTrustedPackages("*");

        log.debug("deserializer created");

        return new DefaultKafkaConsumerFactory<>(props,
                new ErrorHandlingDeserializer<>(new StringDeserializer()),
                new ErrorHandlingDeserializer<>(deserializer)
        );
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> listenerContainerFactory(ConsumerFactory<String, Object> consumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Object>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setIdleBetweenPolls(1_000);
        factory.getContainerProperties().setPollTimeout(5_000);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        factory.setCommonErrorHandler(kafkaErrorHandler());


        var taskExecutor = new SimpleAsyncTaskExecutor("k-consumer-");
        taskExecutor.setConcurrencyLimit(3);

        var concurrentTaskExecutor = new ConcurrentTaskExecutor(taskExecutor);
        factory.getContainerProperties().setListenerTaskExecutor(concurrentTaskExecutor);
        return factory;
    }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler() {
        BackOff fixedBackOff = new FixedBackOff(5000, 3);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler((record, exception) -> {
            log.error("Error occurred while processing record: {}", record, exception);
        }, fixedBackOff);

        errorHandler.addNotRetryableExceptions(SerializationException.class);

        return errorHandler;
    }
}
