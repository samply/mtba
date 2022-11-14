package de.samply.tasks;

import de.samply.file.bundle.PathsBundle;
import de.samply.spring.MtbaConst;
import de.samply.utils.PathsBundleUtils;
import java.net.MalformedURLException;
import java.net.URL;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CBioportalExporterDelegate implements JavaDelegate {

  private final Logger logger = LoggerFactory.getLogger(CBioportalExporterDelegate.class);
  private WebClient webClient;

  public CBioportalExporterDelegate(
      @Value(MtbaConst.CBIOPORTAL_URL_SV) String cBioPortalUrl
  ) throws MalformedURLException {
    this.webClient = createWebClient(cBioPortalUrl);
  }

  @Override
  public void execute(DelegateExecution delegateExecution) throws Exception {

    logger.info("Export to cBioPortal");
    PathsBundle pathsBundle = PathsBundleUtils.getPathsBundleVariable(delegateExecution);
    //TODO
  }

  private WebClient createWebClient(String cBioPortalUrl) throws MalformedURLException {
    //TODO: Set proxy
    return WebClient.builder().baseUrl(getCBioPortalBaseUrl(cBioPortalUrl))
        .defaultHeaders(httpHeaders -> {
          //httpHeaders.set(HttpHeaders.CONTENT_TYPE, "application/xml+fhir");
          //TODO
        }).build();

  }

  private String getCBioPortalBaseUrl(String cBioPortalUrl) throws MalformedURLException {
    return new URL(new URL(cBioPortalUrl), MtbaConst.CBIOPORTAL_API_ROOT_PATH).toString();
  }
}
