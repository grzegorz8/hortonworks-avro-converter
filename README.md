# Avro Converter

This is a Kafka Connect converter which integrates with Hortonworks Schema Registry.

## Implementation

Significant part of the code is copied from Confluent Kafka Connect codebase, in particular AvroData class.

### Known Issues

 * Schema version is not retrieved.
 
 * Class loading conflicts. Hortonworks Schema Registry client's dependency to `org.glassfish` are not loaded.
   Instead, Kafka Connect's `/libs` are loaded.
   ```
   Caused by: org.glassfish.jersey.message.internal.MessageBodyProviderNotFoundException: MessageBodyWriter not found for media type=application/json, type=class com.shaded.hortonworks.registries.schemaregistry.SchemaMetadata, genericType=class com.shaded.hortonworks.registries.schemaregistry.SchemaMetadata.
       at org.glassfish.jersey.message.internal.WriterInterceptorExecutor$TerminalWriterInterceptor.aroundWriteTo(WriterInterceptorExecutor.java:248)
       at org.glassfish.jersey.message.internal.WriterInterceptorExecutor.proceed(WriterInterceptorExecutor.java:163)
       at org.glassfish.jersey.message.internal.MessageBodyFactory.writeTo(MessageBodyFactory.java:1135)
   ```
   In order to alleviate the exception above missing dependecies (`jersey-media-json-jackson-2.27.jar`, `jersey-entity-filtering-2.27.jar`) have to be added to `<KAFKA_HOME>/libs/`.

## Deployment

### Kafka Connect

Put converter jar into Kafka Connect's classpath. Then edit `connect-distributed.properties`.
```
key.converter=io.sqooba.avro.AvroConverter
value.converter=io.sqooba.avro.AvroConverter
schema.registry.url=http://schema-registry:9090/api/v1
schema.name.key.suffix=-key
schema.name.value.suffix=-value
key.converter.schema.registry.url=http://schema-registry:9090/api/v1
key.converter.schema.name.key.suffix=-key
key.converter.schema.name.value.suffix=-value
value.converter.schema.registry.url=http://schema-registry:9090/api/v1
value.converter.schema.name.key.suffix=-key
value.converter.schema.name.value.suffix=-value
# other schema registry properties

# If the schema registry is secured.
schema.registry.client.ssl.protocol=SSL
schema.registry.client.ssl.trustStorePath=xxx
schema.registry.client.ssl.trustStorePassword=xxx
schema.registry.client.ssl.keyStoreLocation=xxx
schema.registry.client.ssl.keyStorePassword=xxx
schema.registry.client.ssl.keyPassword=xxx
```

## Manual end-to-end tests

Build and start docker:
```bash
mvn clean package
docker-compose -f e2e-docker-compose.yml build
docker-compose -f e2e-docker-compose.yml up
```

Add debezium connector:
```bash
curl -v -H "Accept: application/json" -H "Content-Type: application/json" localhost:8083/connectors -d @end-to-end-tests/requests/register-mysql-connector.json
```

Visit `http://localhost:9090` to see registered schemas.
