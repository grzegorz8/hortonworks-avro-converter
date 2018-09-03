package io.sqooba.avro;


import java.util.Map;

public class AvroDataConfig {

  public static final String ENHANCED_AVRO_SCHEMA_SUPPORT_CONFIG = "enhanced.avro.schema.support";
  public static final boolean ENHANCED_AVRO_SCHEMA_SUPPORT_DEFAULT = false;
  public static final String ENHANCED_AVRO_SCHEMA_SUPPORT_DOC =
      "Toggle for enabling/disabling enhanced avro schema support: Enum symbol preservation and "
          + "Package Name awareness";

  public static final String CONNECT_META_DATA_CONFIG = "connect.meta.data";
  public static final boolean CONNECT_META_DATA_DEFAULT = true;
  public static final String CONNECT_META_DATA_DOC =
      "Toggle for enabling/disabling connect converter to add its meta data to the output schema or not";

  public static final String SCHEMAS_CACHE_SIZE_CONFIG = "schemas.cache.config";
  public static final int SCHEMAS_CACHE_SIZE_DEFAULT = 1000;
  public static final String SCHEMAS_CACHE_SIZE_DOC = "Size of the converted schemas cache";

  public static AvroDataConfig ofProperties(Map<String, ?> properties) {
    AvroDataConfigBuilder builder = AvroDataConfigBuilder.anAvroDataConfig();
    if (properties.containsKey(ENHANCED_AVRO_SCHEMA_SUPPORT_CONFIG)) {
      builder.withEnhancedAvroSchemaSupport((Boolean) properties.get(ENHANCED_AVRO_SCHEMA_SUPPORT_CONFIG));
    }
    if (properties.containsKey(CONNECT_META_DATA_CONFIG)) {
      builder.withConnectMetaData((Boolean) properties.get(CONNECT_META_DATA_CONFIG));
    }
    if (properties.containsKey(SCHEMAS_CACHE_SIZE_CONFIG)) {
      builder.withSchemasCacheSize((Integer) properties.get(SCHEMAS_CACHE_SIZE_CONFIG));
    }
    return builder.build();
  }

  private final boolean enhancedAvroSchemaSupport;
  private final boolean connectMetaData;
  private final int schemasCacheSize;

  private AvroDataConfig(boolean enhancedAvroSchemaSupport, boolean connectMetaData, int schemasCacheSize) {
    this.enhancedAvroSchemaSupport = enhancedAvroSchemaSupport;
    this.connectMetaData = connectMetaData;
    this.schemasCacheSize = schemasCacheSize;
  }

  public boolean isEnhancedAvroSchemaSupport() {
    return enhancedAvroSchemaSupport;
  }

  public boolean isConnectMetaData() {
    return connectMetaData;
  }

  public int getSchemasCacheSize() {
    return schemasCacheSize;
  }


  public static final class AvroDataConfigBuilder {
    private boolean enhancedAvroSchemaSupport = ENHANCED_AVRO_SCHEMA_SUPPORT_DEFAULT;
    private boolean connectMetaData = CONNECT_META_DATA_DEFAULT;
    private int schemasCacheSize = SCHEMAS_CACHE_SIZE_DEFAULT;

    private AvroDataConfigBuilder() {
    }

    public static AvroDataConfigBuilder anAvroDataConfig() {
      return new AvroDataConfigBuilder();
    }

    public AvroDataConfigBuilder withEnhancedAvroSchemaSupport(boolean enhancedAvroSchemaSupport) {
      this.enhancedAvroSchemaSupport = enhancedAvroSchemaSupport;
      return this;
    }

    public AvroDataConfigBuilder withConnectMetaData(boolean connectMetaData) {
      this.connectMetaData = connectMetaData;
      return this;
    }

    public AvroDataConfigBuilder withSchemasCacheSize(int schemasCacheSize) {
      this.schemasCacheSize = schemasCacheSize;
      return this;
    }

    public AvroDataConfig build() {
      return new AvroDataConfig(enhancedAvroSchemaSupport, connectMetaData, schemasCacheSize);
    }
  }
}

