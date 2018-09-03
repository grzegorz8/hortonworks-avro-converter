package io.sqooba.avro

import com.hortonworks.registries.schemaregistry.client.SchemaRegistryClient
import org.apache.kafka.connect.data.Schema
import org.apache.kafka.connect.data.SchemaAndValue
import org.apache.kafka.connect.data.SchemaBuilder
import org.apache.kafka.connect.data.Struct
import spock.lang.Specification
import spock.lang.Subject

import java.nio.ByteBuffer

class AvroConverterIntegrationSpec extends Specification {

    @Subject
    AvroConverter converter

    def setup() {
        Map<String, Object> config = new ConfigurationLoader('avro-converter.properties').asMap()
        converter = new AvroConverter(new SchemaRegistryClient(config))
        converter.configure(config, true)
        converter.configure(config, false)
    }

    def "should serialize and deserialize an object"() {
        given:
        Schema schema = SchemaBuilder.struct()
                .name('TestObject')
                .field('int8', SchemaBuilder.int8().defaultValue((byte) 2).doc('int8 field').build())
                .field('int16', Schema.INT16_SCHEMA)
                .field('int32', Schema.INT32_SCHEMA)
                .field('int64', Schema.INT64_SCHEMA)
                .field('float32', Schema.FLOAT32_SCHEMA)
                .field('float64', Schema.FLOAT64_SCHEMA)
                .field('boolean', Schema.BOOLEAN_SCHEMA)
                .field('string', Schema.STRING_SCHEMA)
                .field('bytes', Schema.BYTES_SCHEMA)
                .field('array', SchemaBuilder.array(Schema.STRING_SCHEMA).build())
                .field('map', SchemaBuilder.map(Schema.STRING_SCHEMA, Schema.INT32_SCHEMA).build())
                .field('mapNonStringKeys', SchemaBuilder.map(Schema.INT32_SCHEMA, Schema.INT32_SCHEMA).build())
                .build()
        Struct struct = new Struct(schema)
                .put('int8', (byte) 12)
                .put('int16', (short) 12)
                .put('int32', 12)
                .put('int64', 12L)
                .put('float32', 12.2f)
                .put('float64', 12.2d)
                .put('boolean', true)
                .put('string', 'foo')
                .put('bytes', ByteBuffer.wrap('foo'.getBytes()))
                .put('array', ['a', 'b', 'c'])
                .put('map', ['field': 1])
                .put('mapNonStringKeys', [1: 1])

        when:
        byte[] serializedObject = converter.fromConnectData('dummy', schema, struct)

        then:
        SchemaAndValue schemaAndValue = converter.toConnectData('dummy', serializedObject)
        schemaAndValue.value() == struct
        schemaAndValue.schema() == schema
    }

    def "should preserve schema version"() {
        given:
        String topicName = 'dummy2'
        and:
        Schema schema = SchemaBuilder.struct()
                .name('ShortSchema')
                .field('field1', Schema.STRING_SCHEMA)
                .field('field2', Schema.INT32_SCHEMA)
                .build()
        Struct struct1 = new Struct(schema)
                .put('field1', 'value1')
                .put('field2', 1)
        Struct struct2 = new Struct(schema)
                .put('field1', 'value2')
                .put('field2', 2)

        when:
        byte[] serializedObject1 = converter.fromConnectData(topicName, schema, struct1)
        byte[] serializedObject2 = converter.fromConnectData(topicName, schema, struct2)

        then:
        converter.toConnectData(topicName, serializedObject1)
        converter.toConnectData(topicName, serializedObject2)

        noExceptionThrown()
        // FIXME: Cannot determine schema version.
    }

    def "should evolve schema version"() {
        given:
        String topicName = 'dummy3'
        and:
        Schema schema = SchemaBuilder.struct()
                .name('ShortSchema')
                .field('field1', Schema.STRING_SCHEMA)
                .field('field2', Schema.INT32_SCHEMA)
                .build()
        Struct struct1 = new Struct(schema)
                .put('field1', 'value1')
                .put('field2', 1)
        and:
        Schema newSchema = SchemaBuilder.struct()
                .name('ShortSchema')
                .field('field1', Schema.STRING_SCHEMA)
                .field('field2', Schema.INT32_SCHEMA)
                .field('field3', Schema.OPTIONAL_BOOLEAN_SCHEMA)
                .build()
        Struct struct2 = new Struct(newSchema)
                .put('field1', 'value2')
                .put('field2', 2)
                .put('field3', true)

        when:
        byte[] serializedObject1 = converter.fromConnectData(topicName, schema, struct1)
        byte[] serializedObject2 = converter.fromConnectData(topicName, newSchema, struct2)

        then:
        converter.toConnectData(topicName, serializedObject1)
        converter.toConnectData(topicName, serializedObject2)

        noExceptionThrown()
        // FIXME: Cannot determine schema version.
    }

}
