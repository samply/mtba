package de.samply.blaze;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.samply.spring.MtbaConst;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BlazeStoreClient {

  private final IGenericClient client;

  public BlazeStoreClient(
      @Value(MtbaConst.BLAZE_STORE_URL_SV) String blazeStoreUrl
  ) {
    this.client = createFhirClient(getBlazeStoreFhirApiUrl(blazeStoreUrl));
  }

  private String getBlazeStoreFhirApiUrl(String blazeStoreUrl){
    try {
      return new URL(new URL(blazeStoreUrl), MtbaConst.BLAZE_STORE_API_PATH).toString();
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public void fetchPatients(List<String> patientPseudonyms) {
    //TODO
    patientPseudonyms.forEach(patientPseudonym -> {
      Bundle bundle = fetchPatientBundle(patientPseudonym);
      System.out.println("TODO: extract patient info of bundle");
    });
  }

  private Bundle fetchPatientBundle(String patientPseudonym) {
    return client.search()
        .byUrl("Patient?identifier=" + patientPseudonym)
        .revInclude(new Include("Observation:patient"))
        .revInclude(new Include("Condition:patient"))
        .revInclude(new Include("Specimen:patient"))
        .revInclude(new Include("Procedure:patient"))
        .revInclude(new Include("MedicationStatement:patient"))
        .revInclude(new Include("ClinicalImpression:patient"))
        .returnBundle(Bundle.class)
        .execute();
  }

  private IGenericClient createFhirClient(String blazeStoreUrl) {
    // TODO: set proxy
    return FhirContext.forR4().newRestfulGenericClient(blazeStoreUrl);
  }

}
