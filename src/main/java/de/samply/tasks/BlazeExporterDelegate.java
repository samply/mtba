package de.samply.tasks;

import de.samply.blaze.BlazeResponse;
import de.samply.file.bundle.PathsBundle;
import de.samply.spring.MtbaConst;
import de.samply.utils.PathsBundleUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class BlazeExporterDelegate implements JavaDelegate {

  private final Logger logger = LoggerFactory.getLogger(BlazeExporterDelegate.class);
  private WebClient webClient;

  public BlazeExporterDelegate(
      @Value(MtbaConst.BLAZE_STORE_URL_SV) String blazeStoreUrl) {
    this.webClient = createWebClient(blazeStoreUrl);
  }

  @Override
  public void execute(DelegateExecution delegateExecution) throws Exception {
    logger.info("Export to Blaze");
    PathsBundle pathsBundle = PathsBundleUtils.getPathsBundleVariable(delegateExecution);
    pathsBundle.getAllPaths().forEach(path -> {
      if (Files.exists(path)) {
        sendPathToBlazeStore(path);
      }
    });
  }

  private void sendPathToBlazeStore(Path path) {
    ResponseEntity<BlazeResponse> blazeResponseEntity = webClient.post()
        .uri(MtbaConst.BLAZE_STORE_API_PATH)
        .bodyValue(createBody(path))
        .retrieve()
        .toEntity(BlazeResponse.class)
        .block();
    if (blazeResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
      Arrays.stream(blazeResponseEntity.getBody().getEntry()).forEach(response -> {
        if (response.getResponse().getStatus().startsWith("20")) {
          //TODO
        } else {
          //TODO
        }
      });
    } else {
      //TODO: Handle HTTP Errors
    }
  }

  private String createBody(Path path) {
    try {
      return createBodyWithoutExceptionHandler(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String createBodyWithoutExceptionHandler(Path path) throws IOException {
    StringBuilder stringBuilder = new StringBuilder();
    try (Stream<String> lines = Files.lines(path)) {
      lines.forEach(line -> stringBuilder.append(line));
    }
    return stringBuilder.toString();
  }

  private WebClient createWebClient(String blazeStoreUrl) {
    //TODO: Set proxy
    return WebClient.builder().baseUrl(blazeStoreUrl)
        .defaultHeaders(httpHeaders -> {
          httpHeaders.set(HttpHeaders.CONTENT_TYPE, "application/xml+fhir");
          //TODO
        }).build();
  }

}
