package com.aston.homework.integration;

import com.aston.homework.dto.EventDto;
import com.aston.homework.dto.EventName;
import com.aston.homework.service.EmailService;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@EmbeddedKafka
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
})
public class KafkaConsumerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, EventDto> kafkaTemplate;

    @MockitoBean
    private EmailService emailService;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Captor
    private ArgumentCaptor<EventDto> eventDtoCaptor;

    @TestConfiguration
    static class TestMailConfig {
        @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
        private String bootstrapServers;

        @Bean
        public ProducerFactory<String, EventDto> producerFactory() {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            configProps.put(ProducerConfig.ACKS_CONFIG, "all");
            return new DefaultKafkaProducerFactory<>(configProps);
        }

        @Bean
        public KafkaTemplate<String, EventDto> kafkaTemplate(ProducerFactory<String, EventDto> producerFactory) {
            return new KafkaTemplate<>(producerFactory);
        }
    }

    @Test
    void shouldSendAndReceivedEvent() throws Exception {
        // Given
        EventDto eventDto = new EventDto(EventName.CREATE, "test@recipient.com");
        doNothing().when(emailService).sendUserEventByEmail(eventDtoCaptor.capture());

        // When
        kafkaTemplate.send("user_events_topic", eventDto).get();

        // Then
        verify(emailService, timeout(5000)).sendUserEventByEmail(any(EventDto.class));
        assertEquals("test@recipient.com", eventDtoCaptor.getValue().getEmail());
        assertEquals(EventName.CREATE, eventDtoCaptor.getValue().getEvent());
    }
}
