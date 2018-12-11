package com.github.harry.springboot;


import org.springframework.core.env.EnumerablePropertySource;

import java.util.Properties;
import java.util.Set;


/**
 * Property source wrapper for Config
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class ConfigPropertySource extends EnumerablePropertySource<Properties> {

  private static final String[] EMPTY_ARRAY = new String[0];

  public ConfigPropertySource(String name, Properties source) {
    super(name, source);
  }

  @Override
  public String[] getPropertyNames() {
    Set<String> propertyNames = this.source.stringPropertyNames();
    if (propertyNames.isEmpty()) {
      return EMPTY_ARRAY;
    }
    return propertyNames.toArray(new String[propertyNames.size()]);
  }

  @Override
  public Object getProperty(String name) {
    return this.source.getProperty(name, null);
  }
}
