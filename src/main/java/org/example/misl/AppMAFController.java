package org.example.misl;

import org.example.misl.maf.DataEntryTO;
import org.example.misl.maf.DiscreteIndexTO;
import org.example.misl.maf.MAFDataClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/maf/data")
public class AppMAFController {
  
  @Value("${MISL_SERVICE}")
  private String mislServiceURL;

  /**
   * Stores a MAF data entry.
   *
   * @param entry the entry to store
   * @return 200 if storing was successful
   * @throws Exception if an error occurs
   */
  @PutMapping("/entry")
  public ResponseEntity<String> storeDataEntry(@RequestBody final DataEntryTO entry)
      throws Exception {
    EnvUtil.ConnectionParams params = EnvUtil.getParams(mislServiceURL);
    MAFDataClient client = new MAFDataClient(params.host, params.port);
    try {
      client.storeDataEntry(entry);
    } finally {
      client.shutdown();
    }

    return new ResponseEntity<>("data entry stored successfully", HttpStatus.OK);
  }

  /**
   * Loads a MAF data entry by its ID.
   *
   * @param entryID the data entry ID
   * @return the loaded MAF data entry
   * @throws Exception if an error occurs
   */
  @GetMapping("/entry/{entryID}")
  public ResponseEntity<DataEntryTO> loadDataEntry(@PathVariable("entryID") final String entryID)
      throws Exception {
    EnvUtil.ConnectionParams params = EnvUtil.getParams(mislServiceURL);
    MAFDataClient client = new MAFDataClient(params.host, params.port);
    try {
      DataEntryTO entry = client.loadDataEntry(entryID);
      return new ResponseEntity<>(entry, HttpStatus.OK);
    } finally {
      client.shutdown();
    }
  }

  /**
   * Deletes a MAF data entry.
   *
   * @param entryID the entry ID
   * @return 200 if deleting was successful
   * @throws Exception if an error occurs
   */
  @DeleteMapping("/entry/{entryID}")
  public ResponseEntity<String> deleteDataEntry(@PathVariable("entryID") final String entryID)
      throws Exception {
    EnvUtil.ConnectionParams params = EnvUtil.getParams(mislServiceURL);
    MAFDataClient client = new MAFDataClient(params.host, params.port);
    try {
      client.deleteDataEntry(entryID);
      return new ResponseEntity<>("data entry deleted successfully", HttpStatus.OK);
    } finally {
      client.shutdown();
    }
  }

  /**
   * Stores a MAF discrete index.
   *
   * @param index the index to store
   * @return 200 if storing was successful
   * @throws Exception if an error occurs
   */
  @PutMapping("/discrete")
  public ResponseEntity<String> storeDiscreteIndex(@RequestBody final DiscreteIndexTO index)
      throws Exception {
    EnvUtil.ConnectionParams params = EnvUtil.getParams(mislServiceURL);
    MAFDataClient client = new MAFDataClient(params.host, params.port);
    try {
      client.storeDiscreteIndex(index);
      return new ResponseEntity<>("discrete index stored successfully", HttpStatus.OK);
    } finally {
      client.shutdown();
    }
  }

  /**
   * Loads a specific MAF discrete index.
   *
   * @param partition the partition
   * @param key the key
   * @return the loaded discrete index
   * @throws Exception if an error occurs
   */
  @GetMapping("/discrete/{partition}/{key}")
  public ResponseEntity<DiscreteIndexTO> loadDiscreteIndex(
      @PathVariable("partition") final String partition, @PathVariable("key") final String key)
      throws Exception {
    EnvUtil.ConnectionParams params = EnvUtil.getParams(mislServiceURL);
    MAFDataClient client = new MAFDataClient(params.host, params.port);
    try {
      DiscreteIndexTO index = client.loadDiscreteIndex(partition, key);
      return new ResponseEntity<>(index, HttpStatus.OK);
    } finally {
      client.shutdown();
    }
  }

  /**
   * Deletes a MAF discrete index.
   *
   * @param partition the partition
   * @param key the key
   * @return 200 if successful
   * @throws Exception if an error occurs
   */
  @DeleteMapping("/discrete/{partition}/{key}")
  public ResponseEntity<String> deleteDiscreteIndex(
      @PathVariable("partition") final String partition, @PathVariable("key") final String key)
      throws Exception {
    EnvUtil.ConnectionParams params = EnvUtil.getParams(mislServiceURL);
    MAFDataClient client = new MAFDataClient(params.host, params.port);
    try {
      client.deleteDiscreteIndex(partition, key);
      return new ResponseEntity<>("discrete index deleted successfully", HttpStatus.OK);
    } finally {
      client.shutdown();
    }
  }
}
