FROM    debezium/connect:0.9

RUN     curl -o /kafka/libs/jersey-media-json-jackson-2.27.jar   http://central.maven.org/maven2/org/glassfish/jersey/media/jersey-media-json-jackson/2.27/jersey-media-json-jackson-2.27.jar && \
        curl -o /kafka/libs/jersey-entity-filtering-2.27.jar   http://central.maven.org/maven2/org/glassfish/jersey/ext/jersey-entity-filtering/2.27/jersey-entity-filtering-2.27.jar

RUN     mkdir /kafka/connect/hortonworks-avro-converter
ADD     target/kconnect-hortonworks-avro-converter-1.0-SNAPSHOT.jar /kafka/connect/kconnect-hortonworks-avro-converter/
