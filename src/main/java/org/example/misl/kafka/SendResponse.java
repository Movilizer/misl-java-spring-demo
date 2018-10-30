package org.example.misl.kafka;

public class SendResponse {
  private String response;
  private MessageTO message;

  public SendResponse(final String response, final MessageTO message) {
    this.response = response;
    this.message = message;
  }

  public String getResponse() {
    return this.response;
  }

  public void setResponse(final String response) {
    this.response = response;
  }

  public MessageTO getMessage() {
    return this.message;
  }

  public void setMessage(final MessageTO message) {
    this.message = message;
  }
}
