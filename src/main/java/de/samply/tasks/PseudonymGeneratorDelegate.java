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
import de.samply.pseudonymisation.PatientFieldsUtils;
import de.samply.pseudonymisation.PseudonymisationClient;
import de.samply.spring.MtbaConst;
import de.samply.utils.PathsBundleUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PseudonymGeneratorDelegate implements JavaDelegate {

  private final Logger logger = LoggerFactory.getLogger(PseudonymGeneratorDelegate.class);

  private PseudonymisationClient pseudonymisationClient;
  private PatientFieldsUtils patientFieldsUtils;
  private final CsvUpdaterFactory csvUpdaterFactory = new CsvUpdaterFactoryImpl();
  private final String patientCsvLocalId;
  private final String patientCsvFilename;
  private final String idManagerPseudonymIdType;
  private final Boolean removeIdat;
  private final static String PATIENT_CSV_TEMP_HEADER = "TEMP_PAT_ID";
  private List<String> idatHeaders = new ArrayList<>();
  private Map<String, BiConsumer<PatientFields, String>> headerPatientFieldsMap = new HashMap<>();


  public PseudonymGeneratorDelegate(
      @Value(MtbaConst.PATIENT_CSV_LOCAL_ID_HEADER_SV) String patientCsvLocalId,
      @Value(MtbaConst.PATIENT_CSV_FILENAME_SV) String patientCsvFilename,
      @Value(MtbaConst.PATIENT_CSV_FIRST_NAME_HEADER_SV) String patientCsvFirstNameHeader,
      @Value(MtbaConst.PATIENT_CSV_LAST_NAME_HEADER_SV) String patientCsvLastNameHeader,
      @Value(MtbaConst.PATIENT_CSV_PREVIOUS_NAMES_HEADER_SV) String patientCsvPreviousNamesHeader,
      @Value(MtbaConst.PATIENT_CSV_BIRTHDAY_HEADER_SV) String patientCsvBirthdayHeader,
      @Value(MtbaConst.PATIENT_CSV_CITIZENSHIP_HEADER_SV) String patientCsvCitizenshipHeader,
      @Value(MtbaConst.PATIENT_CSV_GENDER_HEADER_SV) String patientCsvGenderHeader,
      @Value(MtbaConst.ID_MANAGER_PSEUDONYM_ID_TYPE_SV) String idManagerPseudonymIdType,
      @Value(MtbaConst.CSV_REMOVE_IDAT_SV) String removeIdat,
      @Autowired PseudonymisationClient pseudonymisationClient,
      @Autowired PatientFieldsUtils patientFieldsUtils) {

    this.pseudonymisationClient = pseudonymisationClient;
    this.patientFieldsUtils = patientFieldsUtils;
    this.removeIdat = Boolean.valueOf(removeIdat);
    this.patientCsvLocalId = patientCsvLocalId;
    this.patientCsvFilename = patientCsvFilename;
    this.idManagerPseudonymIdType = idManagerPseudonymIdType;

    String[] tempIdatHeaders = {patientCsvFirstNameHeader, patientCsvLastNameHeader,
        patientCsvPreviousNamesHeader, patientCsvBirthdayHeader, patientCsvCitizenshipHeader,
        patientCsvGenderHeader};
    Arrays.stream(tempIdatHeaders).forEach(header -> {
      if (header != null) {
        this.idatHeaders.add(header);
      }
    });

    headerPatientFieldsMap.put(patientCsvFirstNameHeader, PatientFields::setFirstName);
    headerPatientFieldsMap.put(patientCsvLastNameHeader, PatientFields::setLastName);
    headerPatientFieldsMap.put(patientCsvPreviousNamesHeader, PatientFields::setPreviousNames);
    headerPatientFieldsMap.put(patientCsvBirthdayHeader, (patientField, value) ->
        patientField.setBirthdate(patientFieldsUtils.convertBirthdayToIdManager(value)));
    headerPatientFieldsMap.put(patientCsvCitizenshipHeader, PatientFields::setCitizenship);
    headerPatientFieldsMap.put(patientCsvGenderHeader, (patientField, value) ->
        patientField.setGender(patientFieldsUtils.covertGenderToIdManager(value)));
  }

  @Override
  public void execute(DelegateExecution delegateExecution) throws Exception {
    logger.info("Generate pseudonyms");
    PathsBundle pathsBundle = PathsBundleUtils.getPathsBundleVariable(delegateExecution);
    if (containsAlreadyIdManagerPseudonymIdType(pathsBundle)) { // Already pseudonymized
      removeIdatAndTempIdHeaders(pathsBundle);
    } else if (patientCsvLocalId != null) { // Local Id
      fetchPseudonymsFromLocalId(pathsBundle);
    } else { //IDAT
      fetchPseudonymsFromIdat(pathsBundle);
    }
  }

  private void fetchPseudonymsFromLocalId(PathsBundle pathsBundle)
      throws CsvUpdaterFactoryException {
    CsvUpdater csvUpdater = createCsvUpdater(pathsBundle, patientCsvFilename);
    //TODO
  }

  private void fetchPseudonymsFromIdat(PathsBundle pathsBundle)
      throws CsvUpdaterFactoryException, CsvUpdaterException, CsvReaderException, IOException {
    CsvUpdater csvUpdater = createCsvUpdater(pathsBundle, patientCsvFilename);
    // Add temporal id as column. This works as pivot.
    csvUpdater.applyConsumer(new TempIdCsvRecordHeaderValuesConsumer());
    // Read IDAT and pseudonomyze.
    Map<Integer, String> patIdPseudonymMap = pseudonymisationClient.fetchPatIdPseudonym(
        readIdat(pathsBundle, patientCsvFilename));
    // Add Pseudonyms to file.
    csvUpdater.addPivotedCsvRecordHeaderValues(
        createPivotedCsvRecordHeaderValues(patIdPseudonymMap));
    // Remove IDAT and temporal id columns.
    removeIdatAndTempIdHeaders(csvUpdater);
  }

  private CsvUpdater createCsvUpdater(PathsBundle pathsBundle, String filename)
      throws CsvUpdaterFactoryException {
    return csvUpdaterFactory.createCsvUpdater(
        new CsvReaderParameters(patientCsvFilename, pathsBundle));
  }

  private void removeIdatAndTempIdHeaders(PathsBundle pathsBundle)
      throws CsvUpdaterException, CsvUpdaterFactoryException {
    CsvUpdater csvUpdater = createCsvUpdater(pathsBundle, patientCsvFilename);
    removeIdatAndTempIdHeaders(csvUpdater);
  }

  private void removeIdatAndTempIdHeaders(CsvUpdater csvUpdater) throws CsvUpdaterException {
    Set<String> columnsToDelete = (removeIdat) ? getIdatAndTempIdHeaders()
        : new HashSet<>(Arrays.asList(PATIENT_CSV_TEMP_HEADER));
    csvUpdater.deleteColumns(columnsToDelete);
  }

  private Set<String> getIdatAndTempIdHeaders() {
    List<String> headers = new ArrayList<>();
    headers.add(PATIENT_CSV_TEMP_HEADER);
    headers.addAll(idatHeaders);
    return new HashSet<>(headers);
  }

  private PivotedCsvRecordHeaderValues createPivotedCsvRecordHeaderValues(
      Map<Integer, String> patIdPseudonymMap) {
    PivotedCsvRecordHeaderValues pivotedCsvRecordHeaderValues = new PivotedCsvRecordHeaderValues(
        PATIENT_CSV_TEMP_HEADER);
    patIdPseudonymMap.keySet().forEach(patId -> {
      CsvRecordHeaderValues csvRecordHeaderValues = new CsvRecordHeaderValues();
      Map<String, String> headerValueMap = csvRecordHeaderValues.getHeaderValueMap();
      headerValueMap.put(PATIENT_CSV_TEMP_HEADER, patId.toString());
      headerValueMap.put(idManagerPseudonymIdType, patIdPseudonymMap.get(patId));
      pivotedCsvRecordHeaderValues.addCsvRecordHeaderValues(csvRecordHeaderValues);
    });
    return pivotedCsvRecordHeaderValues;
  }

  private class TempIdCsvRecordHeaderValuesConsumer implements CsvRecordHeaderValuesConsumer {

    private Integer counter = 0;

    @Override
    public void accept(CsvRecordHeaderValues csvRecordHeaderValues) throws CsvUpdaterException {
      csvRecordHeaderValues.getHeaderValueMap()
          .put(PATIENT_CSV_TEMP_HEADER, (counter++).toString());
    }

    @Override
    public CsvRecordHeaderOrder prepareHeaders(CsvRecordHeaderOrder csvRecordHeaderOrder)
        throws CsvUpdaterException {
      csvRecordHeaderOrder.addHeaderAtLastPosition(PATIENT_CSV_TEMP_HEADER);
      return csvRecordHeaderOrder;
    }
  }

  private Map<Integer, Patient> readIdat(PathsBundle pathsBundle, String filename)
      throws CsvReaderException, IOException {
    Map<Integer, Patient> patIdPatientMap = new HashMap<>();

    try (CsvReader csvReader = new CsvReaderImpl(
        createCsvReaderParametersToReadIdat(pathsBundle, filename))) {
      csvReader.readCsvRecordHeaderValues().forEach(csvRecordHeaderValues -> {
        Patient patient = createPatient(csvRecordHeaderValues);
        Integer patId = Integer.valueOf(csvRecordHeaderValues.getValue(PATIENT_CSV_TEMP_HEADER));
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
    Path path = pathsBundle.getPath(patientCsvFilename);
    if (Files.exists(path)) {
      try (Stream<String> lines = Files.lines(path)) {
        Optional<String> first = lines.findFirst();
        return (first.isPresent()) ? first.get().contains(idManagerPseudonymIdType) : false;
      }
    }
    return false;
  }

}
