package de.samply.tasks;

import de.samply.file.bundle.PathsBundle;
import de.samply.file.csv.CsvRecordHeaderOrder;
import de.samply.file.csv.CsvRecordHeaderValues;
import de.samply.file.csv.reader.CsvReader;
import de.samply.file.csv.reader.CsvReaderException;
import de.samply.file.csv.reader.CsvReaderImpl;
import de.samply.file.csv.reader.CsvReaderParameters;
import de.samply.file.csv.updater.CsvRecordHeaderValuesConsumer;
import de.samply.file.csv.updater.CsvUpdater;
import de.samply.file.csv.updater.CsvUpdaterException;
import de.samply.file.csv.updater.CsvUpdaterFactory;
import de.samply.file.csv.updater.CsvUpdaterFactoryException;
import de.samply.file.csv.updater.CsvUpdaterFactoryImpl;
import de.samply.file.csv.updater.PivotedCsvRecordHeaderValues;
import de.samply.pseudonymisation.Patient;
import de.samply.pseudonymisation.PatientFields;
import de.samply.pseudonymisation.PseudonymisationClient;
import de.samply.spring.MtbaConst;
import de.samply.utils.PathsBundleUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.python.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PseudonymGeneratorDelegate implements JavaDelegate {

  private final Logger logger = LoggerFactory.getLogger(PseudonymGeneratorDelegate.class);

  private PseudonymisationClient pseudonymisationClient;
  private final CsvUpdaterFactory csvUpdaterFactory = new CsvUpdaterFactoryImpl();
  private final String localIdCsvFilename;
  private final String localIdCsvLocalId;
  private final String idatCsvFilename;
  private final String idManagerPseudonymIdType;
  private final static String IDAT_CSV_TEMP_HEADER = "TEMP_PAT_ID";
  private List<String> idatHeaders = new ArrayList<>();
  private Map<String, BiConsumer<PatientFields, String>> headerPatientFieldsMap = new HashMap<>();


  public PseudonymGeneratorDelegate(
      @Value(MtbaConst.LOCAL_ID_CSV_FILENAME_SV) String localIdCsvFilename,
      @Value(MtbaConst.LOCAL_ID_CSV_LOCAL_ID_HEADER_SV) String localIdCsvLocalId,
      @Value(MtbaConst.IDAT_CSV_FILENAME_SV) String idatCsvFilename,
      @Value(MtbaConst.IDAT_CSV_FIRST_NAME_HEADER_SV) String idatCsvFirstNameHeader,
      @Value(MtbaConst.IDAT_CSV_LAST_NAME_HEADER_SV) String idatCsvLastNameHeader,
      @Value(MtbaConst.IDAT_CSV_PREVIOUS_NAMES_HEADER_SV) String idatCsvPreviousNamesHeader,
      @Value(MtbaConst.IDAT_CSV_BIRTHDAY_HEADER_SV) String idatCsvBirthdayHeader,
      @Value(MtbaConst.IDAT_CSV_CITIZENSHIP_HEADER_SV) String idatCsvCitizenshipHeader,
      @Value(MtbaConst.IDAT_CSV_GENDER_HEADER_SV) String idatCsvGenderHeader,
      @Value(MtbaConst.ID_MANAGER_PSEUDONYM_ID_TYPE_SV) String idManagerPseudonymIdType,
      @Autowired PseudonymisationClient pseudonymisationClient) {

    this.pseudonymisationClient = pseudonymisationClient;
    this.localIdCsvFilename = localIdCsvFilename;
    this.localIdCsvLocalId = localIdCsvLocalId;
    this.idatCsvFilename = idatCsvFilename;
    this.idManagerPseudonymIdType = idManagerPseudonymIdType;
    String[] tempIdatHeaders = {idatCsvFirstNameHeader, idatCsvLastNameHeader,
        idatCsvPreviousNamesHeader, idatCsvBirthdayHeader, idatCsvCitizenshipHeader,
        idatCsvGenderHeader};
    Arrays.stream(tempIdatHeaders).forEach(header -> {
      if (header != null) {
        this.idatHeaders.add(header);
      }
    });
    //headerPatientFieldsMap.put(idatCsvFirstNameHeader, ((patientFields, value) -> patientFields.setFirstName(value)));
    headerPatientFieldsMap.put(idatCsvFirstNameHeader, PatientFields::setFirstName);
    headerPatientFieldsMap.put(idatCsvLastNameHeader, PatientFields::setLastName);
    headerPatientFieldsMap.put(idatCsvPreviousNamesHeader, PatientFields::setPreviousNames);
    headerPatientFieldsMap.put(idatCsvBirthdayHeader, PatientFields::setBirthdate);
    headerPatientFieldsMap.put(idatCsvCitizenshipHeader, PatientFields::setCitizenship);
    headerPatientFieldsMap.put(idatCsvGenderHeader, PatientFields::setGender);
  }

  @Override
  public void execute(DelegateExecution delegateExecution) throws Exception {
    logger.info("Generate pseudonyms");
    PathsBundle pathsBundle = PathsBundleUtils.getPathsBundleVariable(delegateExecution);
    if (containsAlreadyIdManagerPseudonymIdType(pathsBundle)) {
      removeIdatAndTempIdHeaders(pathsBundle);
    } else if (localIdCsvFilename != null) {
      fetchPseudonymsFromLocalId(pathsBundle);
    } else if (idatCsvFilename != null) {
      fetchPseudonymsFromIdat(pathsBundle);
    }
  }

  private void fetchPseudonymsFromLocalId(PathsBundle pathsBundle)
      throws CsvUpdaterFactoryException {
    CsvUpdater csvUpdater = createCsvUpdater(pathsBundle, localIdCsvFilename);
    //TODO
  }

  private void fetchPseudonymsFromIdat(PathsBundle pathsBundle)
      throws CsvUpdaterFactoryException, CsvUpdaterException, CsvReaderException, IOException {
    CsvUpdater csvUpdater = createCsvUpdater(pathsBundle, idatCsvFilename);
    // Add temporal id as column. This works as pivot.
    csvUpdater.applyConsumer(new TempIdCsvRecordHeaderValuesConsumer());
    // Read IDAT and pseudonomyze.
    Map<Integer, String> patIdPseudonymMap = pseudonymisationClient.fetchPatIdPseudonym(
        readIdat(pathsBundle, idatCsvFilename));
    // Add Pseudonyms to file.
    csvUpdater.addPivotedCsvRecordHeaderValues(
        createPivotedCsvRecordHeaderValues(patIdPseudonymMap));
    // Remove IDAT and temporal id columns.
    removeIdatAndTempIdHeaders(csvUpdater);
  }

  private CsvUpdater createCsvUpdater(PathsBundle pathsBundle, String filename)
      throws CsvUpdaterFactoryException {
    return csvUpdaterFactory.createCsvUpdater(
        createCsvReaderParametersForUpdater(pathsBundle, idatCsvFilename));
  }

  private void removeIdatAndTempIdHeaders(PathsBundle pathsBundle)
      throws CsvUpdaterException, CsvUpdaterFactoryException {
    String filename = getActiveFilename();
    if (filename != null) {
      CsvUpdater csvUpdater = createCsvUpdater(pathsBundle, filename);
      csvUpdater.deleteColumns(getIdatAndTempIdHeaders());
    }
  }

  private void removeIdatAndTempIdHeaders(CsvUpdater csvUpdater) throws CsvUpdaterException {
    csvUpdater.deleteColumns(getIdatAndTempIdHeaders());
  }

  private Set<String> getIdatAndTempIdHeaders() {
    List<String> headers = new ArrayList<>();
    headers.add(IDAT_CSV_TEMP_HEADER);
    headers.addAll(idatHeaders);
    return new HashSet<>(headers);
  }

  private PivotedCsvRecordHeaderValues createPivotedCsvRecordHeaderValues(
      Map<Integer, String> patIdPseudonymMap) {
    PivotedCsvRecordHeaderValues pivotedCsvRecordHeaderValues = new PivotedCsvRecordHeaderValues(
        IDAT_CSV_TEMP_HEADER);
    patIdPseudonymMap.keySet().forEach(patId -> {
      CsvRecordHeaderValues csvRecordHeaderValues = new CsvRecordHeaderValues();
      Map<String, String> headerValueMap = csvRecordHeaderValues.getHeaderValueMap();
      headerValueMap.put(IDAT_CSV_TEMP_HEADER, patId.toString());
      headerValueMap.put(idManagerPseudonymIdType, patIdPseudonymMap.get(patId));
      pivotedCsvRecordHeaderValues.addCsvRecordHeaderValues(csvRecordHeaderValues);
    });
    return pivotedCsvRecordHeaderValues;
  }

  private class TempIdCsvRecordHeaderValuesConsumer implements CsvRecordHeaderValuesConsumer {

    private Integer counter = 0;

    @Override
    public void accept(CsvRecordHeaderValues csvRecordHeaderValues) throws CsvUpdaterException {
      csvRecordHeaderValues.getHeaderValueMap().put(IDAT_CSV_TEMP_HEADER, (counter++).toString());
    }

    @Override
    public CsvRecordHeaderOrder prepareHeaders(CsvRecordHeaderOrder csvRecordHeaderOrder)
        throws CsvUpdaterException {
      csvRecordHeaderOrder.addHeaderAtLastPosition(IDAT_CSV_TEMP_HEADER);
      return csvRecordHeaderOrder;
    }
  }

  CsvReaderParameters createCsvReaderParametersForUpdater(PathsBundle pathsBundle,
      String filename) {
    CsvReaderParameters csvReaderParameters = new CsvReaderParameters();
    csvReaderParameters.setPathsBundle(pathsBundle);
    csvReaderParameters.setFilename(filename);
    return csvReaderParameters;
  }

  private Map<Integer, Patient> readIdat(PathsBundle pathsBundle, String filename)
      throws CsvReaderException, IOException {
    Map<Integer, Patient> patIdPatientMap = new HashMap<>();

    try (CsvReader csvReader = new CsvReaderImpl(
        createCsvReaderParametersToReadIdat(pathsBundle, filename))) {
      csvReader.readCsvRecordHeaderValues().forEach(csvRecordHeaderValues -> {
        Patient patient = createPatient(csvRecordHeaderValues);
        Integer patId = Integer.valueOf(csvRecordHeaderValues.getValue(IDAT_CSV_TEMP_HEADER));
        patIdPatientMap.put(patId, patient);
      });
    }

    return patIdPatientMap;
  }

  private Patient createPatient(CsvRecordHeaderValues csvRecordHeaderValues) {
    PatientFields patientFields = new PatientFields();
    csvRecordHeaderValues.getHeaderValueMap().keySet().forEach(header -> {
      BiConsumer<PatientFields, String> consumer = headerPatientFieldsMap.get(header);
      if (consumer != null) {
        consumer.accept(patientFields, csvRecordHeaderValues.getValue(header));
      }
    });
    List<String> idTypes = Arrays.asList(idManagerPseudonymIdType);
    return new Patient(patientFields, idTypes, null);
  }

  CsvReaderParameters createCsvReaderParametersToReadIdat(PathsBundle pathsBundle,
      String filename) {
    CsvReaderParameters csvReaderParameters = new CsvReaderParameters();
    csvReaderParameters.setPathsBundle(pathsBundle);
    csvReaderParameters.setHeaders(getIdatAndTempIdHeaders());
    csvReaderParameters.setFilename(filename);
    return csvReaderParameters;
  }

  private boolean containsAlreadyIdManagerPseudonymIdType(PathsBundle pathsBundle)
      throws IOException {
    String filename = getActiveFilename();
    if (filename != null) {
      try (Stream<String> lines = Files.lines(pathsBundle.getPath(filename))) {
        Optional<String> first = lines.findFirst();
        return (first.isPresent()) ? first.get().contains(idManagerPseudonymIdType) : false;
      }
    }
    return false;
  }

  private String getActiveFilename() {
    return (idatCsvFilename != null) ? idatCsvFilename : localIdCsvFilename;
  }

}
