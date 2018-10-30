package org.example.misl.maf;

import com.google.protobuf.ByteString;
import com.movilizer.node.api.maf.data.DataEntry;
import com.movilizer.node.api.maf.data.DataEntryDeleteRequest;
import com.movilizer.node.api.maf.data.DataEntryDeleteResponse;
import com.movilizer.node.api.maf.data.DataEntryLoadRequest;
import com.movilizer.node.api.maf.data.DataEntryLoadResponse;
import com.movilizer.node.api.maf.data.DataEntryStoreRequest;
import com.movilizer.node.api.maf.data.DataEntryStoreResponse;
import com.movilizer.node.api.maf.data.DiscreteIndex;
import com.movilizer.node.api.maf.data.DiscreteIndexDeleteRequest;
import com.movilizer.node.api.maf.data.DiscreteIndexDeleteResponse;
import com.movilizer.node.api.maf.data.DiscreteIndexLoadRequest;
import com.movilizer.node.api.maf.data.DiscreteIndexLoadResponse;
import com.movilizer.node.api.maf.data.DiscreteIndexStoreRequest;
import com.movilizer.node.api.maf.data.DiscreteIndexStoreResponse;
import com.movilizer.node.api.maf.data.MAFDataGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.example.misl.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Implements client-side gRPC calls to the MAFData server. */
public class MAFDataClient extends GrpcClient {

  private static final Logger LOG = LoggerFactory.getLogger(MAFDataClient.class);

  private static final String RPC_FAILED_MESSAGE = "RPC failed: {}";
  private static final int DEFAULT_TTL = 8400;

  private final MAFDataGrpc.MAFDataBlockingStub blockingStub;

  public MAFDataClient(final String host, final int port) {
    this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
  }

  private MAFDataClient(final ManagedChannel channel) {
    super(channel);
    this.blockingStub = MAFDataGrpc.newBlockingStub(channel);
  }

  /**
   * Stores a new MAF data entry.
   *
   * @param to the MAF data entry to store
   */
  public void storeDataEntry(final DataEntryTO to) {
    Map<String, ByteString> attributes = new HashMap<>();
    for (Entry<String, String> entry : to.getAttributes().entrySet()) {
      String key = entry.getKey();
      String val = entry.getValue();
      attributes.put(key, ByteString.copyFromUtf8(val));
    }

    DataEntry entry =
                    DataEntry.newBuilder()
                                    .setId(ByteString.copyFromUtf8(to.getId()))
                                    .putAllAttributes(attributes)
                                    .build();

    DataEntryStoreRequest request =
                    DataEntryStoreRequest.newBuilder()
                                    .setEntry(entry)
                                    .setTtlInSeconds(DEFAULT_TTL)
                                    .setAppend(to.isAppend())
                                    .build();

    DataEntryStoreResponse response;
    try {
      response = this.blockingStub.storeDataEntry(request);
    } catch (StatusRuntimeException e) {
      LOG.error(RPC_FAILED_MESSAGE, e.getStatus());
      throw e;
    }
    LOG.info("DataEntryStoreResponse: {}", response.getMessage());
  }

  /**
   * Loads a specific MAF data entry by its id.
   *
   * @param entryID the entry ID
   * @return the MAF data entry
   */
  public DataEntryTO loadDataEntry(final String entryID) {
    DataEntryLoadRequest request =
                    DataEntryLoadRequest.newBuilder().setId(ByteString.copyFromUtf8(entryID)).build();

    try {
      DataEntryLoadResponse response = this.blockingStub.loadDataEntry(request);

      DataEntryTO to = new DataEntryTO();
      to.setId(entryID);

      DataEntry dataEntry = response.getEntry();

      for (Entry<String, ByteString> entry : dataEntry.getAttributesMap().entrySet()) {
        String key = entry.getKey();
        ByteString val = entry.getValue();
        to.addAttribute(key, val.toStringUtf8());
      }

      return to;
    } catch (StatusRuntimeException e) {
      LOG.error(RPC_FAILED_MESSAGE, e.getStatus());
      throw e;
    }
  }

  /**
   * Deletes a specific MAF data entry by its id.
   *
   * @param entryID the entry ID
   */
  public void deleteDataEntry(final String entryID) {
    DataEntryDeleteRequest request =
                    DataEntryDeleteRequest.newBuilder().setId(ByteString.copyFromUtf8(entryID)).build();

    DataEntryDeleteResponse response;
    try {
      response = this.blockingStub.deleteDataEntry(request);
    } catch (StatusRuntimeException e) {
      LOG.error(RPC_FAILED_MESSAGE, e.getStatus());
      throw e;
    }
    LOG.info("DataEntryDeleteResponse: {}", response.getMessage());
  }

  /**
   * Stores a MAF discrete index.
   *
   * @param to the MAF discrete index to store
   */
  public void storeDiscreteIndex(final DiscreteIndexTO to) {
    DiscreteIndex index =
                    DiscreteIndex.newBuilder()
                                    .setKey(to.getKey())
                                    .setPartition(ByteString.copyFromUtf8(to.getPartition()))
                                    .setValue(ByteString.copyFromUtf8(to.getValue()))
                                    .build();

    DiscreteIndexStoreRequest request =
                    DiscreteIndexStoreRequest.newBuilder().setTtlInSeconds(DEFAULT_TTL).setIndex(index).build();

    DiscreteIndexStoreResponse response;
    try {
      response = this.blockingStub.storeDiscreteIndex(request);
    } catch (StatusRuntimeException e) {
      LOG.error(RPC_FAILED_MESSAGE, e.getStatus());
      throw e;
    }
    LOG.info("DiscreteIndexStoreResponse: {}", response.getMessage());
  }

  /**
   * Loads a specific MAF discrete index.
   *
   * @param partition the partition
   * @param key the key
   * @return the MAF discrete index
   */
  public DiscreteIndexTO loadDiscreteIndex(final String partition, final String key) {

    DiscreteIndexLoadRequest request =
                    DiscreteIndexLoadRequest.newBuilder()
                                    .setPartition(ByteString.copyFromUtf8(partition))
                                    .setKey(key)
                                    .build();
    try {
      DiscreteIndexLoadResponse response = this.blockingStub.loadDiscreteIndex(request);

      DiscreteIndex index = response.getIndex();

      return new DiscreteIndexTO(partition, key, index.getValue().toStringUtf8());
    } catch (StatusRuntimeException e) {
      LOG.error(RPC_FAILED_MESSAGE, e.getStatus());
      throw e;
    }
  }

  /**
   * Deletes a specific MAF discrete index.
   *
   * @param partition the partition
   * @param key the key
   */
  public void deleteDiscreteIndex(final String partition, final String key) {

    DiscreteIndexDeleteRequest request =
                    DiscreteIndexDeleteRequest.newBuilder()
                                    .setPartition(ByteString.copyFromUtf8(partition))
                                    .setKey(key)
                                    .build();

    DiscreteIndexDeleteResponse response;
    try {
      response = this.blockingStub.deleteDiscreteIndex(request);
    } catch (StatusRuntimeException e) {
      LOG.error(RPC_FAILED_MESSAGE, e.getStatus());
      throw e;
    }
    LOG.info("DiscreteIndexDeleteResponse: {}", response.getMessage());
  }
}