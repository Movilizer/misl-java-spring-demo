package org.example.misl.monitoring;

public class SimpleMessageTO {

  private long systemID;
  private String value;
  private String details;

  private EventType type;
  private Sender sender;
  private Severity severity;

  public SimpleMessageTO() {}

  protected enum EventType {
    PORTAL_SYSTEM
  }

  protected enum Sender {
    PORTAL,
    MAF,
    MDS
  }

  protected enum Severity {
    INFO,
    DEBUG,
    ERROR,
    FATAL
  }

  public static class Builder {
    private long systemID;
    private String value;
    private String details;
    private EventType type;
    private Sender sender;
    private Severity severity;

    public Builder systemID(final long systemID) {
      this.systemID = systemID;
      return this;
    }

    public Builder value(final String value) {
      this.value = value;
      return this;
    }

    public Builder details(final String details) {
      this.details = details;
      return this;
    }

    public Builder type(final EventType type) {
      this.type = type;
      return this;
    }

    public Builder sender(final Sender sender) {
      this.sender = sender;
      return this;
    }

    public Builder severity(final Severity severity) {
      this.severity = severity;
      return this;
    }

    public SimpleMessageTO build() {
      return new SimpleMessageTO(this);
    }
  }

  private SimpleMessageTO(final Builder builder) {
    this.systemID = builder.systemID;
    this.value = builder.value;
    this.details = builder.details;
    this.type = builder.type;
    this.sender = builder.sender;
    this.severity = builder.severity;
  }

  public long getSystemID() {
    return this.systemID;
  }

  public String getValue() {
    return this.value;
  }

  public String getDetails() {
    return this.details;
  }

  public EventType getType() {
    return this.type;
  }

  public Sender getSender() {
    return this.sender;
  }

  public Severity getSeverity() {
    return this.severity;
  }
}