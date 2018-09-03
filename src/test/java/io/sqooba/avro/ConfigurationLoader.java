package io.sqooba.avro;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationConverter;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class ConfigurationLoader {

  private final Configuration configuration;

  public ConfigurationLoader(String fileName) throws ConfigurationException {
    Parameters params = new Parameters();
    final FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
        new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
            .configure(params.properties().setFileName(fileName));

    this.configuration = builder.getConfiguration();
  }

  public Map<String, Object> asMap() {
    return ConfigurationConverter.getMap(configuration).entrySet().stream()
        .collect(toMap(
            e -> e.getKey().toString(),
            Map.Entry::getValue
        ));
  }

}
