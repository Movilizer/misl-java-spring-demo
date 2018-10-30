package org.example.misl.kafka;

import java.util.List;

public class ConsumeResponse {
  private String response;
  private List<MessageTO> messages;

  public ConsumeResponse(final String response, final List<MessageTO> messages) {
    this.response = response;
    this.messages = messages;
  }

  public String getResponse() {
    return this.response;
  }

  public void setResponse(final String response) {
    this.response = response;
  }

  public List<MessageTO> getMessages() {
    return this.messages;
  }

  public void setMessages(final List<MessageTO> messages) {
    this.messages = messages;
  }
}
