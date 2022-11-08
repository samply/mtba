package de.samply.pseudonymisation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.samply.spring.MtbaConst;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PseudonymisationClient {

  private final Logger logger = LoggerFactory.getLogger(PseudonymisationClient.class);
  private WebClient webClient;


  public PseudonymisationClient(
      @Value(MtbaConst.ID_MANAGER_API_KEY_SV) String apiKey,
      @Value(MtbaConst.ID_MANAGER_URL_SV) String idManagerUrl) {
    this.webClient = createWebClient(apiKey, idManagerUrl);
  }

  public Map<Integer, String> fetchPatIdPseudonym(Map<Integer, Patient> patIdPatientMap) {
    Map<Integer, String> patIdPseudonymMap = new HashMap<>();

    List<Integer> patIds = new ArrayList<>(patIdPatientMap.keySet());
    List<Patient> patients = new ArrayList<>();
    patIds.forEach(patId -> patients.add(patIdPatientMap.get(patId)));

    // TODO: Paging
    ResponseEntity<IdManagerResponse[]> responseEntity = webClient.post()
        .uri(MtbaConst.ID_MANAGER_GET_IDS_PATH)
        .bodyValue(createBody(patients))
        .retrieve()
        .toEntity(IdManagerResponse[].class)
        .block();
    if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
      fetchPseudonyms(responseEntity.getBody(), patIdPseudonymMap, patIds);
    } else {
      // TODO: Handle Http Errors
    }
    return patIdPseudonymMap;
  }

  private void fetchPseudonyms(IdManagerResponse[] idManagerResponseArray,
      Map<Integer, String> patIdPseudonymMap, List<Integer> patIds) {
    AtomicInteger counter = new AtomicInteger(0);
    Arrays.stream(idManagerResponseArray).forEach(idManagerResponse ->
        patIdPseudonymMap.put(patIds.get(counter.getAndIncrement()),
            fetchPseudonym(idManagerResponse)));
  }

  private String fetchPseudonym(IdManagerResponse idManagerResponse) {
    return idManagerResponse.ids().get(0).idString();
  }

  private String createBody(List<Patient> patients) {
    try {
      String body = new ObjectMapper().writeValueAsString(patients);
      logger.debug(body);
      return body;
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private WebClient createWebClient(String apiKey, String idManagerUrl) {
    //TODO: set proxy
    return WebClient.builder().baseUrl(idManagerUrl)
        .defaultHeaders(httpHeaders -> {
          httpHeaders.set(HttpHeaders.CONTENT_TYPE, "application/json");
          httpHeaders.set(MtbaConst.ID_MANAGER_API_KEY_HEADER, apiKey);
        }).build();
  }

}
