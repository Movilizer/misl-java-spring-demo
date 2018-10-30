package org.example.misl;

import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;

public abstract class GrpcClient {

  private final ManagedChannel channel;

  protected GrpcClient(final ManagedChannel channel) {
    this.channel = channel;
  }

  /**
   * Shuts down the gRPC channel.
   *
   * @throws InterruptedException when interrupted
   */
  public void shutdown() throws InterruptedException {
    this.channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }
}
