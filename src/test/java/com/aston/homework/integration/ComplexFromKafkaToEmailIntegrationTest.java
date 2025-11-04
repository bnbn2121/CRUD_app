package com.aston.homework.integration;

import com.aston.homework.dto.EventDto;
import com.aston.homework.dto.EventName;
import com.aston.homework.service.EmailService;
import com.aston.homework.service.impl.KafkaConsumerService;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EmbeddedKafka
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
})
public class ComplexFromKafkaToEmailIntegrationTest {
    private GreenMail greenMail;

    @Autowired
    private KafkaTemplate<String, EventDto> kafkaTemplate;

    @Autowired
    private EmailService emailService;

    @Autowired
    private KafkaConsumerService kafkaConsumerService;

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

        @Bean
        public JavaMailSender javaMailSender() {
            JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
            javaMailSender.setHost("localhost");
            javaMailSender.setPort(3025);
            javaMailSender.getJavaMailProperties().put("mail.smtp.auth", "false");
            javaMailSender.getJavaMailProperties().put("mail.smtp.starttls.enable", "false");
            return javaMailSender;
        }
    }

    @BeforeEach
    void startMailServer() {
        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.start();
    }

    @AfterEach
    void stopMailServer() {
        greenMail.stop();
    }

    @Test
    void shouldSendAndReceivedEvent() throws Exception {
        // Given
        EventDto eventDto = new EventDto(EventName.CREATE, "test@recipient.com");

        // When
        kafkaTemplate.send("user_events_topic", eventDto);
        await().atMost(1000, TimeUnit.MILLISECONDS)
                .pollInterval(200, TimeUnit.MILLISECONDS)
                .until(() -> greenMail.getReceivedMessages().length > 0);

        // Then
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertTrue(receivedMessages.length == 1);
        Message message = receivedMessages[0];
        assertEquals(message.getSubject(), "Notification message");
        assertEquals(message.getContent().toString(), "Здравствуйте! Ваш аккаунт был успешно создан");
        assertEquals(message.getAllRecipients()[0].toString(), "test@recipient.com");
    }

    @Test
    void shouldHandleInvalidEvent() throws Exception {
        // Given
        EventDto invalidEvent = new EventDto(EventName.FOR_TEST, "test@mail.com");

        // When
        kafkaTemplate.send("user_events_topic", invalidEvent);
        Thread.sleep(3000);

        // Then
        assertEquals(0, greenMail.getReceivedMessages().length);
    }
}
