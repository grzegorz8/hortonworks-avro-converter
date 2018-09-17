package io.sqooba.avro

import com.hortonworks.registries.schemaregistry.SchemaCompatibility
import spock.lang.Specification
import spock.lang.Subject

class ConfigTransformerSpec extends Specification {

    @Subject
    ConfigTransformer transformer = new ConfigTransformer()

    def "should transform schema compatibility property"() {
        given:
        Map<String, ?> config = ['schema.compatibility': configValue]
        when:
        Map<String, Object> transformedConfig = transformer.transform(config)
        then:
        transformedConfig.get('schema.compatibility') == transformedValue

        where:
        configValue | transformedValue
        'BOTH'      | SchemaCompatibility.BOTH
        'FORWARD'   | SchemaCompatibility.FORWARD
        'BACKWARD'  | SchemaCompatibility.BACKWARD
    }

    def "should group schema.registry.client.ssl properties into map"() {
        given:
        Map<String, ?> config = [
                'schema.registry.client.ssl.keyStorePath'      : 'a',
                'schema.registry.client.ssl.keyStorePassword'  : 'b',
                'schema.registry.client.ssl.trustStorePath'    : 'c',
                'schema.registry.client.ssl.trustStorePassword': 'd'
        ]
        when:
        Map<String, Object> transformedConfig = transformer.transform(config)
        then:
        transformedConfig.get('schema.registry.client.ssl') == [
                'keyStorePath'      : 'a',
                'keyStorePassword'  : 'b',
                'trustStorePath'    : 'c',
                'trustStorePassword': 'd'
        ]
    }

    def "should preserve other properties"() {
        given:
        Map<String, ?> config = [
                'property1': 'a',
                'property2': 'b',
                'property3': 'c',
                'property4': 'd'
        ]
        when:
        Map<String, Object> transformedConfig = transformer.transform(config)
        then:
        transformedConfig == config
    }

}
