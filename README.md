# libraryInventaryWithKafka
API to consume a library inventary data and send to kafka to other microservice catch from 

Result after test sending message through API with Insomnia aplication.
Result:
2021-01-24 15:38:41.159  INFO 13564 --- [ad | producer-1] c.c.l.producer.LibraryEventProducer      : Message send successfully for the key: null and the value is {"libraryEventId":null,"book":{"bookId":487,"bookName":"Kafka Using Spring Boot","bookAuthor":"Pablo"}}, partition is 0
2021-01-24 15:39:49.284  INFO 13564 --- [ad | producer-1] c.c.l.producer.LibraryEventProducer      : Message send successfully for the key: null and the value is {"libraryEventId":null,"book":{"bookId":555,"bookName":"Kafka Using Spring Boot","bookAuthor":"Pablo"}}, partition is 1


Kafka Embedded for test in Java is delay so much to up, and the aplication giver a time over sometimes.
How to solve this?
I put a Thread sleep for now, but I'm looking for a better way.





#Criar tópico
kafka-topics --bootstrap-server localhost:9092 --topic <nome_topico> --create --partitions 3 --replication-factor 1

#Acrescentar partições em um tópico
kafka-topics --alter --bootstrap-server localhost:9092 --topic <nome_topico> --partitions <qtd>

#Listar tópicos
kafka-topics --bootstrap-server localhost:9092 --list

#Detalhes do tópico
kafka-topics --bootstrap-server localhost:9092 --topic <nome_topico> --describe

#Deletar tópico (Não funciona no Windows) 
kafka-topics --bootstrap-server localhost:9092 --topic <nome_topico> --delete

#Enviar mensagem via linha de comando:
kafka-console-producer --broker-list 127.0.0.1:9092 --topic <nome_topico>

#Consumir mensagens via linha de comando:
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic <nome_topico>

#Consumir mensagens via linha de comando (desde o inicio):
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic <nome_topico> --from-beginning

#Consumir mensagens em grupo
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic <nome_topico> --group <group-name>

#Mostrar grupos
kafka-consumer-groups --bootstrap-server localhost:9092 --list

#Visualizar status das entregas (lag) por grupo:
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group <group-name>

#Reiniciar o offset do grupo para tópico específico
kafka-consumer-groups --bootstrap-server localhost:9092 --group <group-name> --reset-offsets --to-earliest --execute --topic <nome_topico>

#Reiniciar o offset do grupo para todos os tópicos
kafka-consumer-groups --bootstrap-server localhost:9092 --group <group-name> --reset-offsets --to-earliest --execute --all-topics
