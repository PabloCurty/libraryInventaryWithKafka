package com.curty.library_inventary_kafka.integration.controller;

import com.curty.library_inventary_kafka.domain.Book;
import com.curty.library_inventary_kafka.domain.LibraryEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    void postLibraryEventTest() {

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

        //execution
        ResponseEntity<LibraryEvent> responseEntity = restTemplate
                .exchange("/v1/libraryevent", HttpMethod.POST, request, LibraryEvent.class);

        //verification
        //imported assertEquals JUnit at Static variable
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }
}
