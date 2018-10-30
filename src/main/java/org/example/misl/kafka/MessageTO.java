package org.example.misl.kafka;

/**
 * @author Nick Penkov <nikolai.penkov@honeywell.com>
 */
public class MessageTO {
  private String content;

  public MessageTO() {}

  public MessageTO(final String value) {
    this.content = value;
  }

  public String getContent() {
    return this.content;
  }

  public void setContent(final String value) {
    this.content = value;
  }
}