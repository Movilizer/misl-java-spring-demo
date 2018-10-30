package org.example.misl.monitoring;

import com.movilizer.monitoring.api.Message;
import com.movilizer.monitoring.api.MonitoringGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.misl.GrpcClient;

public class MonitoringClient extends GrpcClient {

  private final MonitoringGrpc.MonitoringBlockingStub blockingStub;

  public MonitoringClient(final String host, final int port) {
    this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
  }

  private MonitoringClient(final ManagedChannel channel) {
    super(channel);
    this.blockingStub = MonitoringGrpc.newBlockingStub(channel);
  }

  public void logMessage(final SimpleMessageTO message) {

    Message request =
                    Message.newBuilder()
                                    .setCreationTime(System.currentTimeMillis())
                                    .setMessage(message.getValue())
                                    .setSystemId(message.getSystemID())
                                    .setSeverity(getSeverity(message))
                                    .setSender(getSender(message))
                                    .setEventType(getEventType(message))
                                    .build();

    this.blockingStub.log(request);
  }

  private Message.Sender.Type getSender(final SimpleMessageTO message) {
    switch (message.getSender()) {
      case MAF:
        return Message.Sender.Type.MAF;
      case MDS:
        return Message.Sender.Type.MDS;
      case PORTAL:
      default:
        return Message.Sender.Type.PORTAL;
    }
  }

  private Message.Severity.Type getSeverity(final SimpleMessageTO message) {
    switch (message.getSeverity()) {
      case DEBUG:
        return Message.Severity.Type.DEBUG;
      case ERROR:
        return Message.Severity.Type.ERROR;
      case FATAL:
        return Message.Severity.Type.FATAL;
      case INFO:
      default:
        return Message.Severity.Type.INFO;
    }
  }

  private Message.Event.Type getEventType(final SimpleMessageTO message) {
    switch (message.getType()) {
      case PORTAL_SYSTEM:
      default:
        return Message.Event.Type.PORTAL_SYSTEM;
    }
  }
}