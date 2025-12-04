# event-vs-batch

Dieses Repo demonstriert zwei verschiedene Arten der "Orchestration": Zum einen event-basiert, bei dem jeder Prozessor
auf
seine eigenen Abhängigkeiten schaut, und immer dann anfängt zu prozessieren, wenn ein geeignetes Objekt als Verfügbar
gemeldet wird. Auf der anderen Seite soll Batch-Processing mittels Spring-Batch dargestellt werden.

## Idee

In dieser Applikation gibt es drei Prozessoren. Ziel ist es eine Menge von Entitäten in drei Prozessschritten
hintereinander zu bearbeiten. Dabei werden sie jedes mal um neue daten angereichert. So wird aus der `BaseEntity` eine
`EnrichedEntity`, eine `TwiceEnrichedEntity` und eine `TripleEnrichedEntity`. Dazu gibt es entsprechende Prozessoren in
`service/Enricher`. Leider dauert die Prozessierung ein bisschen Zeit - und manchmal schlägt sie auch fehl.

### Applikationsbeschreibung
Starten der Applikation:
* Die Applikation wird gestartet z.B. in der IDE mit dem Run-Knopf - oder per `./gradlew bootRun` im Folder "./demo"
* Kafka und Kafbat-UI starten - hierzu im repository root `docker compose up -d` ausführen

Mittels der Swagger-UI unter `http://localhost:8080/swagger-ui/index.html` kann der aktuelle Füllstand der jeweiligen
Tabellen eingesehen werden (`GET /status`). Die Ausgangslage
kann hergestellt werden, indem `n` BaseEntitties in die Datenbank geschrieben werden (`POST /initializeDatabase` ).
Die Prozessierung kann mittels `POST /startProcessingEventBased` bzw. `POST /startProcessingBatched` gestartet werden.

## Starten der Applikation

Um die Demo-Applikation im event-Fall zu benutzen:
* Die Applikation kann über die Swagger-UI gesteuert werden. Zuerst Entities anlegen, dann die Prozessierung starten.
* Zum Debuggen können einzelne KafkaMessages gesendet werden (`POST /sendKafkaMessage`)
* Der Status der Prozessierung kann über die Swagger-UI eingesehen werden.

## event

Um die Demo-Applikation im event-Fall zu benutzen:
* Man sieht wie sich nach und nach die Füllstände der verschiedenen Enrichements füllen. Zu bemerken ist dabei, dass einzelne Events sehr früh bereits
  tripleEnriched sind - und zwar bevor alle Events überhaupt fertig enriched sind
* Weiterhin kann die Situation in Kafka über die Kafka-UI kafbat inspiziert werden. Dazu auf `http://localhost:9090/` navigieren

## batch
* Man muss sich leider aufgrund eines der Demo geschuldeten Problems fehlende Flyway-Anbindung und aktuellen Problemen mit dem 
  automatischen Anlegen der Batch Job Tabellen händisch um die Batch Job Tabellen kümmern. Dazu wurde die DB von inmemory H2 
  auf eine file-basierte H2-Datenbank umgestellt. Man muss ich dann mit der DB verbinden (url ist in der appilication.properties-file zu finden)
  und das sql-Script `demo/src/main/resources/init_job_tables.sql` ausführen.
