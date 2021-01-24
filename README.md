# libraryInventaryWithKafka
API to consume a library inventary data and send to kafka to other microservice catch from 

Result after test sending message through API with Insomnia aplication.
Result:
2021-01-24 15:38:41.159  INFO 13564 --- [ad | producer-1] c.c.l.producer.LibraryEventProducer      : Message send successfully for the key: null and the value is {"libraryEventId":null,"book":{"bookId":487,"bookName":"Kafka Using Spring Boot","bookAuthor":"Pablo"}}, partition is 0
2021-01-24 15:39:49.284  INFO 13564 --- [ad | producer-1] c.c.l.producer.LibraryEventProducer      : Message send successfully for the key: null and the value is {"libraryEventId":null,"book":{"bookId":555,"bookName":"Kafka Using Spring Boot","bookAuthor":"Pablo"}}, partition is 1
