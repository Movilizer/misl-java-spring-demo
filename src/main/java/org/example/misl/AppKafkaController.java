package org.example.misl;

import com.google.protobuf.ByteString;
import com.movilizer.node.api.kafka.Message;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.example.misl.kafka.ConsumeResponse;
import org.example.misl.kafka.KafkaClient;
import org.example.misl.kafka.MessageTO;
import org.example.misl.kafka.SendResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class AppKafkaController {

  @Value("${MISL_SERVICE}")
  private String mislServiceURL;

  /**
   * Sends a message to a specific Kafka topic and partition.
   *
   * @param topic the topic
   * @param partition the partition
   * @param message the message
   * @return the server response
   * @throws InterruptedException if sending was interrupted
   */
  @GetMapping("/send/{topic}/{partition}")
  public ResponseEntity<SendResponse> send(
                  @PathVariable(value = "topic") final String topic,
                  @PathVariable(value = "partition") final int partition,
                  @RequestParam(value = "msg") final String message)
                  throws InterruptedException {

    EnvUtil.ConnectionParams params = EnvUtil.getParams(mislServiceURL);
    KafkaClient client = new KafkaClient(params.host, params.port);

    Message msg = Message.newBuilder().setContent(ByteString.copyFromUtf8(message)).build();

    try {
      client.sendMessage(topic, partition, Arrays.asList(msg));
    } finally {
      client.shutdown();
    }

    return new ResponseEntity<>(
                    new SendResponse(
                                    "message sent successfully", new MessageTO(msg.getContent().toStringUtf8())),
                    HttpStatus.OK);
  }

  /**
   * Consumes from a specific Kafka topic for 5 seconds.
   *
   * @param topic the topic
   * @param groupID the consumer group ID
   * @return the consumed messages
   * @throws InterruptedException if consuming was interrupted
   */
  @GetMapping("/consume/{topic}/{group}")
  public ResponseEntity<ConsumeResponse> consume(
                  @PathVariable(value = "topic") final String topic,
                  @PathVariable(value = "group") final String groupID)
                  throws InterruptedException {
    EnvUtil.ConnectionParams params = EnvUtil.getParams(mislServiceURL);
    KafkaClient client = new KafkaClient(params.host, params.port);
    try {
      List<MessageTO> messages = client.consume(UUID.randomUUID().toString(), groupID, topic);
      return new ResponseEntity<>(
                      new ConsumeResponse(messages.size() + " messages consumed", messages), HttpStatus.OK);
    } finally {
      client.shutdown();
    }
  }
}