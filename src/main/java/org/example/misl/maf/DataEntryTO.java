package org.example.misl.maf;

/**
 * @author Nick Penkov <nikolai.penkov@honeywell.com>
 */
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DataEntryTO {

  private String id;
  private final Map<String, String> attributes = new HashMap<>();
  private boolean append;

  public String getId() {
    return this.id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public Map<String, String> getAttributes() {
    return Collections.unmodifiableMap(this.attributes);
  }

  public void addAttribute(final String key, final String value) {
    this.attributes.put(key, value);
  }

  public void addAttributes(final Map<String, String> attributes) {
    this.attributes.putAll(attributes);
  }

  public boolean isAppend() {
    return this.append;
  }

  public void setAppend(final boolean append) {
    this.append = append;
  }
}