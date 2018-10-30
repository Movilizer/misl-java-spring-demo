package org.example.misl.maf;

public class DiscreteIndexTO {

  private String partition;
  private String key;
  private String value;

  public DiscreteIndexTO() {}

  public DiscreteIndexTO(final String partition, final String key, final String value) {
    this.partition = partition;
    this.key = key;
    this.value = value;
  }

  public String getPartition() {
    return this.partition;
  }

  public void setPartition(final String partition) {
    this.partition = partition;
  }

  public String getKey() {
    return this.key;
  }

  public void setKey(final String key) {
    this.key = key;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(final String value) {
    this.value = value;
  }
}