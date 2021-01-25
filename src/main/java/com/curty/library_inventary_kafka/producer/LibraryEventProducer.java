package com.curty.library_inventary_kafka.producer;

import com.curty.library_inventary_kafka.domain.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class LibraryEventProducer {

    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    //Topic can be defined in application.yml, but we have a buildProducer method with topic in the signature
    String topic = "library-events";

    //Send a message in synchronous way using
    public SendResult<Integer, String> sendLibraryEventSynchronous(LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);
        SendResult<Integer, String> sendResult = null;
        try {
            sendResult = kafkaTemplate.sendDefault(key,value).get(1, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            log.error("ExecutionException/InterruptedException Error sending the message anda the exception is {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception Error sending the message anda the exception is {}", e.getMessage());
            throw e;
        }
        return sendResult;
    }

    //Send a message in Asynchronous way with de "send default" method and capturing with the Listenable Future object
    // The "send default" method pass just a key and value
    //Send only key and value. Handle with failure or success
    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {

        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);
        ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.sendDefault(key,value);
        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(key, value, ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key, value, result);
            }
        });
    }

    //Send a message in Asynchronous way with de "send" method and capturing with the Listenable Future object
    // The "send" method pass topic, partition, key, value and headers in optional way,
    // and we can pass an object from "Producer Record" class, I was able to study this two ways of this signature
    // is this case we only passed key, value, topic and header.
    //Using a ProducerRecord object to send topic, partition, key, value and header
    public void sendLibraryEvent_Approach2(LibraryEvent libraryEvent) throws JsonProcessingException {

        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        ProducerRecord<Integer, String> producerRecord = buildProducerRecord(key, value, topic);

        ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.send(producerRecord);
        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(key, value, ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key, value, result);
            }
        });
    }

    //Build a producer record to send like an object
    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {
        List<Header> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));
        return new ProducerRecord<>(topic, null, key, value, recordHeaders);
    }

    //Error handle
    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Error sending the message anda the exception is {}", ex.getMessage());
        try {
            throw ex;
        }catch (Throwable throwable){
            log.error("Error in OnFailure: {}", throwable.getMessage());
        }
    }

    //Success handle
    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        log.info("Message send successfully for the key: {} and the value is {}, partition is {}"
                , key, value, result.getRecordMetadata().partition());
    }

}
