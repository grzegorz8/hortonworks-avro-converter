package io.sqooba.avro;

import com.hortonworks.registries.schemaregistry.SchemaCompatibility;

import java.util.HashMap;
import java.util.Map;

import static com.hortonworks.registries.schemaregistry.serdes.avro.AbstractAvroSnapshotSerializer.SERDES_PROTOCOL_VERSION;
import static com.hortonworks.registries.schemaregistry.serdes.avro.kafka.KafkaAvroSerializer.SCHEMA_COMPATIBILITY;
import static java.util.stream.Collectors.toMap;

class ConfigTransformer {

  Map<String, Object> transform(Map<String, ?> configs) {
    Map<String, Object> result = new HashMap<>(configs);

    // String value needs to bo converter to an enum value.
    if (configs.containsKey(SCHEMA_COMPATIBILITY)) {
      if (configs.get(SCHEMA_COMPATIBILITY) instanceof String) {
        result.put(SCHEMA_COMPATIBILITY, SchemaCompatibility.valueOf((String) configs.get(SCHEMA_COMPATIBILITY)));
      }
    }
    // serdes.protocol.version should be a number.
    if (configs.containsKey(SERDES_PROTOCOL_VERSION)) {
      if (configs.get(SERDES_PROTOCOL_VERSION) instanceof String) {
        result.put(SERDES_PROTOCOL_VERSION, Integer.valueOf((String) configs.get(SERDES_PROTOCOL_VERSION)));
      }
    }

    // schema.registry.client.ssl.* properties should be passed as a property,
    // where the key is 'schema.registry.client.ssl' and the value is a map of properties without the 's.r.c.s' prefix.
    Map<String, Object> clientSslProperties = configs.entrySet().stream()
        .filter(entry -> entry.getKey().startsWith("schema.registry.client.ssl."))
        .collect(toMap(
            e -> e.getKey().replaceFirst("schema\\.registry\\.client\\.ssl\\.", ""),
            Map.Entry::getValue
        ));
    if (!clientSslProperties.isEmpty()) {
      result.put("schema.registry.client.ssl", clientSslProperties);
    }

    return result;
  }

}
