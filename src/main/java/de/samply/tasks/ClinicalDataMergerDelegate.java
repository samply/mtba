package de.samply.tasks;

import de.samply.blaze.BlazeStoreClient;
import de.samply.file.bundle.PathsBundle;
import de.samply.file.bundle.PathsBundleManager;
import de.samply.file.bundle.PathsBundleManagerImpl;
import de.samply.file.csv.reader.CsvReader;
import de.samply.file.csv.reader.CsvReaderException;
import de.samply.file.csv.reader.CsvReaderImpl;
import de.samply.file.csv.reader.CsvReaderParameters;
import de.samply.spring.MtbaConst;
import de.samply.utils.PathsBundleUtils;
import de.samply.utils.TemporalDirectoryManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClinicalDataMergerDelegate implements JavaDelegate {

  private final Logger logger = LoggerFactory.getLogger(ClinicalDataMergerDelegate.class);
  private TemporalDirectoryManager temporalDirectoryManager;
  private BlazeStoreClient blazeStoreClient;
  private String patientFilename;
  private String idManagerPseudonymIdType;

  public ClinicalDataMergerDelegate(
      @Autowired TemporalDirectoryManager temporalDirectoryManager,
      @Autowired BlazeStoreClient blazeStoreClient,
      @Value(MtbaConst.PATIENT_CSV_FILENAME_SV) String patientFilename,
      @Value(MtbaConst.ID_MANAGER_PSEUDONYM_ID_TYPE_SV) String idManagerPseudonymIdType) {
    this.temporalDirectoryManager = temporalDirectoryManager;
    this.blazeStoreClient = blazeStoreClient;
    this.patientFilename = patientFilename;
    this.idManagerPseudonymIdType = idManagerPseudonymIdType;
  }

  @Override
  public void execute(DelegateExecution delegateExecution) throws Exception {

    logger.info("Fetch clinical data and merge");
    PathsBundle pathsBundle = PathsBundleUtils.getPathsBundleVariable(delegateExecution);
    PathsBundleManager pathsBundleManager = new PathsBundleManagerImpl(pathsBundle.getDirectory());
    pathsBundle = pathsBundleManager.copyPathsBundleToOutputFolder(pathsBundle,
        temporalDirectoryManager.createTemporalDirectory());
    fetchClinicalDataFromBlazeStore(getPatientPseudonyms(pathsBundle));
    //TODO
  }

  private List<String> getPatientPseudonyms(PathsBundle pathsBundle){
    try {
      return getPatientPseudonymsWithoutExceptionManagement(pathsBundle);
    } catch (CsvReaderException e) {
      throw new RuntimeException(e);
    }
  }

  private List<String> getPatientPseudonymsWithoutExceptionManagement(PathsBundle pathsBundle)
      throws CsvReaderException {
    List<String> patientPseudonymList = new ArrayList<>();
    CsvReaderParameters csvReaderParameters = new CsvReaderParameters(patientFilename, pathsBundle);
    csvReaderParameters.setHeaders(
        new HashSet<>(Arrays.asList(idManagerPseudonymIdType)));
    CsvReader patientFileReader = new CsvReaderImpl(csvReaderParameters);
    patientFileReader.readCsvRecordHeaderValues().forEach(csvRecordHeaderValues -> {
      String pseudonym = csvRecordHeaderValues.getValue(idManagerPseudonymIdType);
      patientPseudonymList.add(pseudonym);
    });
    return patientPseudonymList;
  }

  private void fetchClinicalDataFromBlazeStore(List<String> patientPseudonyms){
    blazeStoreClient.fetchPatients(patientPseudonyms);
  }

}
