package com.curty.library_inventary_kafka.unit.controller;

import com.curty.library_inventary_kafka.controller.LibraryEventsController;
import com.curty.library_inventary_kafka.domain.Book;
import com.curty.library_inventary_kafka.domain.LibraryEvent;
import com.curty.library_inventary_kafka.producer.LibraryEventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LibraryEventsController.class)
@AutoConfigureMockMvc
public class LibraryEventControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    LibraryEventProducer libraryEventProducer;

    @Test
    void postLibraryEventUnitTest() throws Exception {
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

        String json = objectMapper.writeValueAsString(libraryEvent);

        //execution
        doNothing().when(libraryEventProducer)
                .sendLibraryEvent_Approach2(isA(LibraryEvent.class));

        //verification
        mockMvc.perform(post("/v1/libraryevent")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    // when book null the controller advice answer 'errorMessage : book - must not be null'"
    @Test
    void postLibraryEventUnitTest_BookNull_4xx() throws Exception {
        //scenario
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(null)
                .build();

        String json = objectMapper.writeValueAsString(libraryEvent);
        String expectedError = "book - must not be null";
        //execution
        doNothing().when(libraryEventProducer)
                .sendLibraryEvent_Approach2(isA(LibraryEvent.class));

        //verification
        mockMvc.perform(post("/v1/libraryevent")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
        .andExpect(content().string(expectedError));
    }

    // when some book field is null or Blank the controller advice answer 'errorMessage : book - must not be null'"
    @Test
    void postLibraryEventUnitTest_BookFieldNullOrBlank_4xx() throws Exception {
        //scenario
        Book book = Book.builder()
                .bookId(null)
                .bookAuthor(null)
                .bookName("Kafka Spring Boot")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book)
                .build();

        String json = objectMapper.writeValueAsString(libraryEvent);
        String expectedError = "book.bookAuthor - must not be blank, book.bookId - must not be null";

        //execution
        doNothing().when(libraryEventProducer)
                .sendLibraryEvent_Approach2(isA(LibraryEvent.class));

        //verification
        mockMvc.perform(post("/v1/libraryevent")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
        .andExpect(content().string(expectedError));
    }
}
