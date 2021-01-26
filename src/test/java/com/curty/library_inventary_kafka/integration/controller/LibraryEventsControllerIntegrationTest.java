package com.curty.library_inventary_kafka.integration.controller;

import com.curty.library_inventary_kafka.domain.Book;
import com.curty.library_inventary_kafka.domain.LibraryEvent;
import com.curty.library_inventary_kafka.domain.LibraryEventType;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

//If you not provide the port, all the time we will launch the application in default port 88
//It's can make a conflict, happened with me
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"}, partitions = 3)
//Set the bootstrap servers configuration in this embedded kafka catch what is in application.yml
//The value to set is in the EmbeddedKafkaBroker class, is the Static variable SPRING_EMBEDDED_KAFKA_BROKERS
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap-servers=${spring.embedded.kafka.brokers}"})
public class LibraryEventsControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<Integer, String> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> configs =
                new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker ));
        consumer = new DefaultKafkaConsumerFactory<>(
                configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    void postLibraryEventTest() throws InterruptedException {

        //scenario
        Book book = Book.builder()
                .bookId(123)
                .bookAuthor("Pablo")
                .bookName("Kafka Spring Boot")
                .build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book)
                .build();
        //We need to pass in restTemplate.exchange a LibraryEvent in a HttpEntity and HttpHeaders way
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

        String expectedValue = "{\"libraryEventId\":null,\"libraryEventType\":\"NEW\",\"book\":{\"bookId\":123,\"bookName\":\"Kafka Spring Boot\",\"bookAuthor\":\"Pablo\"}}";

        //execution
        ResponseEntity<LibraryEvent> responseEntity = restTemplate
                .exchange("/v1/libraryevent", HttpMethod.POST, request, LibraryEvent.class);
        ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "library-events");
        Thread.sleep(3000);
        String value = consumerRecord.value();

        //verification
        //imported assertEquals JUnit at Static variable is a problem to understand the code later
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        Assertions.assertEquals(expectedValue, value);
    }
}
