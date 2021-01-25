package com.curty.library_inventary_kafka.controller;

import com.curty.library_inventary_kafka.domain.LibraryEvent;
import com.curty.library_inventary_kafka.domain.LibraryEventType;
import com.curty.library_inventary_kafka.producer.LibraryEventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@Slf4j
public class LibraryEventsController {

    @Autowired
    LibraryEventProducer libraryEventProducer;

    //POST
    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        //invoke kafka producer
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        log.info("before sendLibraryEvent");
        /*
        libraryEventProducer.sendLibraryEvent(libraryEvent);
        SendResult<Integer, String> sendResult = libraryEventProducer.sendLibraryEventSynchronous(libraryEvent);
        log.info("SendResult is {}", sendResult.toString());
        */
        libraryEventProducer.sendLibraryEvent_Approach2(libraryEvent);
        log.info("after sendLibraryEvent");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    //PUT

}
