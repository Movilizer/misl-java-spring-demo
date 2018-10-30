package org.example.misl.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.example.misl.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.movilizer.node.api.kafka.ConsumeRequest;
import com.movilizer.node.api.kafka.ConsumeRequest.Command;
import com.movilizer.node.api.kafka.KafkaGrpc;
import com.movilizer.node.api.kafka.Message;
import com.movilizer.node.api.kafka.Response;
import com.movilizer.node.api.kafka.SendRequest;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

/** Implements client-side gRPC calls to the Kafka server. */
public class KafkaClient extends GrpcClient {
  private static final Logger LOG = LoggerFactory.getLogger(KafkaClient.class);

  private final KafkaGrpc.KafkaStub asyncStub;

  private KafkaClient(final ManagedChannel channel) {
    super(channel);
    this.asyncStub = KafkaGrpc.newStub(channel);
  }

  public KafkaClient(final String host, final int port) {
    this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
  }

  /**
   * Sends messages to Kafka.
   *
   * @param topic the topic
   * @param partitionKey the partition key
   * @param messages the messages
   * @throws InterruptedException if sending was interrupted
   */
  public void sendMessage(final String topic, final int partitionKey, final List<Message> messages)
                  throws InterruptedException {
    final CountDownLatch finishLatch = new CountDownLatch(1);

    StreamObserver<Response> responseObserver =
                    new StreamObserver<Response>() {
                      @Override
                      public void onCompleted() {
                        LOG.info("Finished sending Kafka messages");
                        finishLatch.countDown();
                      }

                      @Override
                      public void onError(final Throwable t) {
                        Status status = Status.fromThrowable(t);
                        LOG.warn("Sending Kafka message failed: {}", status);
                        finishLatch.countDown();
                      }

                      @Override
                      public void onNext(final Response response) {
                        LOG.info("Response from server: '{}'", response.getMessage());
                      }
                    };

    StreamObserver<SendRequest> requestObserver = this.asyncStub.send(responseObserver);

    try {
      SendRequest request =
                      SendRequest.newBuilder()
                                      .setTopic(topic)
                                      .setPartitionKey(partitionKey)
                                      .addAllMessages(messages)
                                      .build();
      requestObserver.onNext(request);
    } catch (RuntimeException e) {
      requestObserver.onError(e);
      throw e;
    }

    requestObserver.onCompleted();

    finishLatch.await(1, TimeUnit.MINUTES);
  }

  /**
   * Consumes Kafka messages from the specified topic.
   *
   * @param consumerID the consumer ID
   * @param groupID the group ID
   * @param topic the topic
   * @throws InterruptedException the interrupted exception
   */
  public List<MessageTO> consume(final String consumerID, final String groupID, final String topic)
                  throws InterruptedException {
    final CountDownLatch finishLatch = new CountDownLatch(1);

    List<MessageTO> messages = new ArrayList<>();

    StreamObserver<Message> responseObserver =
                    new StreamObserver<Message>() {
                      @Override
                      public void onCompleted() {
                        LOG.info("Finished consuming Kafka messages");
                        finishLatch.countDown();
                      }

                      @Override
                      public void onError(final Throwable t) {
                        Status status = Status.fromThrowable(t);
                        LOG.warn("Consuming Kafka message failed: {}", status);
                        finishLatch.countDown();
                      }

                      @Override
                      public void onNext(final Message message) {
                        String messageContent = message.getContent().toStringUtf8();

                        messages.add(new MessageTO(messageContent));
                        LOG.info("Received message: '{}'", messageContent);
                      }
                    };

    StreamObserver<ConsumeRequest> requestObserver = this.asyncStub.consume(responseObserver);

    ConsumeRequest startRequest =
                    ConsumeRequest.newBuilder()
                                    .setConsumerId(consumerID)
                                    .setGroupId(groupID)
                                    .setTopic(topic)
                                    .setCommand(Command.START)
                                    .build();

    ConsumeRequest stopRequest =
                    ConsumeRequest.newBuilder()
                                    .setConsumerId(consumerID)
                                    .setGroupId(groupID)
                                    .setTopic(topic)
                                    .setCommand(Command.STOP)
                                    .build();

    LOG.info("Consuming from topic '{}' started", topic);

    requestObserver.onNext(startRequest);

    TimeUnit.SECONDS.sleep(5);

    requestObserver.onNext(stopRequest);

    LOG.info("Consuming from topic '{}' stopped", topic);

    requestObserver.onCompleted();

    finishLatch.await(1, TimeUnit.MINUTES);

    return messages;
  }
}