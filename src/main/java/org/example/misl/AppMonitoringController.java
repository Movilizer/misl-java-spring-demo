package org.example.misl;

import org.example.misl.monitoring.MonitoringClient;
import org.example.misl.monitoring.SimpleMessageTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mon")
public class AppMonitoringController {

  @Value("${MISL_SERVICE}")
  private String mislServiceURL;

  @PostMapping("/log")
  public ResponseEntity<String> log(@RequestBody final SimpleMessageTO message)
                  throws InterruptedException {
    EnvUtil.ConnectionParams params = EnvUtil.getParams(mislServiceURL);
    MonitoringClient client = new MonitoringClient(params.host, params.port);
    try {
      client.logMessage(message);
    } finally {
      client.shutdown();
    }
    return new ResponseEntity<>("message logged successfully", HttpStatus.CREATED);
  }
}