package io.sqooba.avro;

import com.google.common.annotations.VisibleForTesting;
import com.hortonworks.registries.schemaregistry.client.SchemaRegistryClient;
import com.hortonworks.registries.schemaregistry.serdes.avro.kafka.KafkaAvroDeserializer;
import com.hortonworks.registries.schemaregistry.serdes.avro.kafka.KafkaAvroSerializer;
import org.apache.avro.generic.IndexedRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.storage.Converter;

import java.util.Map;

public class AvroConverter implements Converter {

  private SchemaRegistryClient client;
  private KafkaAvroSerializer serializer;
  private KafkaAvroDeserializer deserializer;
  private AvroData avroData;

  public AvroConverter() {
  }

  @VisibleForTesting
  AvroConverter(SchemaRegistryClient client) {
    this.client = client;
  }

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    if (client == null) {
      client = new SchemaRegistryClient(configs);
    }

    avroData = new AvroData(AvroDataConfig.ofProperties(configs));
    serializer = new KafkaAvroSerializer(client);
    deserializer = new KafkaAvroDeserializer(client);
    serializer.configure(configs, isKey);
    deserializer.configure(configs, isKey);
  }

  @Override
  public byte[] fromConnectData(String topic, Schema schema, Object value) {
    Object object = avroData.fromConnectData(schema, value);
    /*
     * Nulls represent deletes. In addition, Kafka has a special meaning for deletion in a topic, especially
     * with log compaction enables.
     * We need to bypass schema registration for null.
     */
    if (object == null) {
      return null;
    }
    return serializer.serialize(topic, object);
  }

  @Override
  public SchemaAndValue toConnectData(String topic, byte[] value) {
    if (value == null) {
      return SchemaAndValue.NULL;
    }
    Object deserialized = deserializer.deserialize(topic, value);
    if (deserialized == null) {
      return SchemaAndValue.NULL;
    } else if (deserialized instanceof IndexedRecord) {
      return avroData.toConnectData(((IndexedRecord) deserialized).getSchema(), deserialized);
    } else if (deserialized instanceof NonRecordContainer) {
      return avroData.toConnectData(((NonRecordContainer) deserialized).getSchema(), ((NonRecordContainer) deserialized).getValue());
    } else {
      throw new DataException(String.format("Unsupported type returned during deserialization of topic %s.", topic));
    }
  }

}
