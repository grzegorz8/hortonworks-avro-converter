# Avro Converter

This is a Kafka Connect converter which integrates with Hortonworks Schema Registry.

## Implementation

Significant part of the code is copied from Confluent Kafka Connect codebase, in particular AvroData class.

### Known Issues

 * Schema version is not retrieved.

## Deployment

### Kafka Connect

Put converter jar into Kafka Connect's classpath. Then Edit `connect-distributed.properties`.
```
key.converter=io.sqooba.avro.AvroConverter
value.converter=io.sqooba.avro.AvroConverter
key.converter.schema.registry.url=http://localhost:9090/api/v1
key.converter.schema.name.key.suffix=-key
key.converter.schema.name.value.suffix=-value
# other schema registry properties

# If the schema registry is secured.
schema.registry.client.ssl.protocol=SSL
schema.registry.client.ssl.trustStorePath=xxx
schema.registry.client.ssl.trustStorePassword=xxx
schema.registry.client.ssl.keyStoreLocation=xxx
schema.registry.client.ssl.keyStorePassword=xxx
schema.registry.client.ssl.keyPassword=xxx

```
