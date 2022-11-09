package de.samply.tasks;

import de.samply.file.bundle.PathsBundle;
import de.samply.file.bundle.PathsBundleManager;
import de.samply.file.bundle.PathsBundleManagerImpl;
import de.samply.file.csv.CsvRecordHeaderValues;
import de.samply.file.csv.reader.CsvReader;
import de.samply.file.csv.reader.CsvReaderException;
import de.samply.file.csv.reader.CsvReaderImpl;
import de.samply.file.csv.reader.CsvReaderParameters;
import de.samply.file.csv.updater.CsvUpdater;
import de.samply.file.csv.updater.CsvUpdaterException;
import de.samply.file.csv.updater.CsvUpdaterFactory;
import de.samply.file.csv.updater.CsvUpdaterFactoryException;
import de.samply.file.csv.updater.CsvUpdaterFactoryImpl;
import de.samply.file.csv.updater.CsvUpdaterImpl;
import de.samply.file.csv.updater.CsvUpdaterParameters;
import de.samply.file.csv.updater.PivotedCsvRecordHeaderValues;
import de.samply.spring.MtbaConst;
import de.samply.utils.PathsBundleUtils;
import de.samply.utils.TemporalDirectoryManager;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FhirConverterDelegate implements JavaDelegate {

  private final Logger logger = LoggerFactory.getLogger(FhirConverterDelegate.class);
  private final CsvUpdaterFactory csvUpdaterFactory = new CsvUpdaterFactoryImpl();
  private TemporalDirectoryManager temporalDirectoryManager;
  private String dataMutationFile;
  private String scriptInterpreter;
  private String patientFilename;
  private String patientFilePatientIdHeader;
  private String idManagerPseudonymIdType;
  private String dataMutationFileSampleIdHeader;
  private String sampleFilename;
  private String sampleFileSampleIdHeader;
  private String sampleFilePatientIdHeader;

  public FhirConverterDelegate(
      @Value(MtbaConst.MUTATIONS_CSV_FILENAME_SV) String dataMutationFile,
      @Value(MtbaConst.MUTATIONS_CSV_SCRIPT_INTERPRETER_SV) String scriptInterpreter,
      @Value(MtbaConst.PATIENT_CSV_FILENAME_SV) String patientFilename,
      @Value(MtbaConst.PATIENT_CSV_PATIENT_ID_HEADER_SV) String patientFilePatientIdHeader,
      @Value(MtbaConst.ID_MANAGER_PSEUDONYM_ID_TYPE_SV) String idManagerPseudonymIdType,
      @Value(MtbaConst.MUTATIONS_CSV_SAMPLE_ID_HEADER_SV) String dataMutationFileSampleIdHeader,
      @Value(MtbaConst.SAMPLE_CSV_FILENAME_SV) String sampleFilename,
      @Value(MtbaConst.SAMPLE_CSV_SAMPLE_ID_HEADER_SV) String sampleFileSamplyIdHeader,
      @Value(MtbaConst.SAMPLE_CSV_PATIENT_ID_HEADER_SV) String sampleFilePatientIdHeader,

      @Autowired TemporalDirectoryManager temporalDirectoryManager
  ) {
    this.dataMutationFile = dataMutationFile;
    this.scriptInterpreter = scriptInterpreter;
    this.patientFilename = patientFilename;
    this.patientFilePatientIdHeader = patientFilePatientIdHeader;
    this.idManagerPseudonymIdType = idManagerPseudonymIdType;
    this.temporalDirectoryManager = temporalDirectoryManager;
    this.dataMutationFileSampleIdHeader = dataMutationFileSampleIdHeader;
    this.sampleFilename = sampleFilename;
    this.sampleFileSampleIdHeader = sampleFileSamplyIdHeader;
    this.sampleFilePatientIdHeader = sampleFilePatientIdHeader;
  }

  @Override
  public void execute(DelegateExecution delegateExecution) throws Exception {
    logger.info("Convert to FHIR");
    PathsBundle pathsBundle = PathsBundleUtils.getPathsBundleVariable(delegateExecution);
    // Copy files in new temporal directory
    PathsBundleManager pathsBundleManager = new PathsBundleManagerImpl(pathsBundle.getDirectory());
    pathsBundle = pathsBundleManager.copyPathsBundleToOutputFolder(pathsBundle,
        temporalDirectoryManager.createTemporalDirectory());

    addPseudonymToDataMutationFile(pathsBundle);
    executeScript(pathsBundle.getPath(dataMutationFile));
    //TODO
  }

  private void addPseudonymToDataMutationFile(PathsBundle pathsBundle)
      throws CsvReaderException, CsvUpdaterFactoryException, CsvUpdaterException {
    Map<String, String> patientIdPseudonymMap = fetchPatientIdPseudonymMap(pathsBundle);
    Map<String, String> sampleIdPatientIdMap = fetchSampleIdPatientIdMap(pathsBundle);
    Map<String, String> sampleIdPseudonymMap = convertToSampleIdPseudonymMap(patientIdPseudonymMap,
        sampleIdPatientIdMap);
    addPseudonymToDataMutationFile(pathsBundle, sampleIdPseudonymMap);
  }

  private Map<String, String> fetchPatientIdPseudonymMap(PathsBundle pathsBundle)
      throws CsvReaderException {
    Map<String, String> patientIdPseudonymMap = new HashMap<>();
    CsvReaderParameters csvReaderParameters = new CsvReaderParameters(patientFilename, pathsBundle);
    csvReaderParameters.setHeaders(
        new HashSet<>(Arrays.asList(patientFilePatientIdHeader, idManagerPseudonymIdType)));
    CsvReader patientFileReader = new CsvReaderImpl(csvReaderParameters);
    patientFileReader.readCsvRecordHeaderValues().forEach(csvRecordHeaderValues -> {
      String patientId = csvRecordHeaderValues.getValue(patientFilePatientIdHeader);
      String pseudonym = csvRecordHeaderValues.getValue(idManagerPseudonymIdType);
      patientIdPseudonymMap.put(patientId, pseudonym);
    });
    return patientIdPseudonymMap;
  }

  private Map<String, String> fetchSampleIdPatientIdMap(PathsBundle pathsBundle)
      throws CsvReaderException {
    Map<String, String> sampleIdPatientIdMap = new HashMap<>();
    CsvReaderParameters csvReaderParameters = new CsvReaderParameters(sampleFilename, pathsBundle);
    csvReaderParameters.setHeaders(new HashSet<>(Arrays.asList(sampleFilePatientIdHeader,
        sampleFileSampleIdHeader)));
    CsvReader sampleFileReader = new CsvReaderImpl(csvReaderParameters);
    sampleFileReader.readCsvRecordHeaderValues().forEach(csvRecordHeaderValues -> {
      String patientId = csvRecordHeaderValues.getValue(sampleFilePatientIdHeader);
      String sampleId = csvRecordHeaderValues.getValue(sampleFileSampleIdHeader);
      sampleIdPatientIdMap.put(sampleId, patientId);
    });
    return sampleIdPatientIdMap;
  }

  private Map<String, String> convertToSampleIdPseudonymMap(
      Map<String, String> patientIdPseudonymMap, Map<String, String> sampleIdPatientIdMap) {
    Map<String, String> sampleIdPseudonymMap = new HashMap<>();
    sampleIdPatientIdMap.keySet().forEach(sampleId -> sampleIdPseudonymMap.put(sampleId,
        patientIdPseudonymMap.get(sampleIdPatientIdMap.get(sampleId))));
    return sampleIdPseudonymMap;
  }

  private void addPseudonymToDataMutationFile(PathsBundle pathsBundle,
      Map<String, String> sampleIdPseudonymMap)
      throws CsvUpdaterFactoryException, CsvUpdaterException {
    CsvUpdater csvUpdater = csvUpdaterFactory.createCsvUpdater(
        new CsvReaderParameters(dataMutationFile, pathsBundle));
    csvUpdater.addPivotedCsvRecordHeaderValues(
        createPivotedCsvRecordHeaderValuesToAddPseudonymToDataMutationFile(sampleIdPseudonymMap));
  }

  private PivotedCsvRecordHeaderValues createPivotedCsvRecordHeaderValuesToAddPseudonymToDataMutationFile(
      Map<String, String> sampleIdPseudonymMap) {
    PivotedCsvRecordHeaderValues pivotedCsvRecordHeaderValues = new PivotedCsvRecordHeaderValues(
        dataMutationFileSampleIdHeader);
    sampleIdPseudonymMap.keySet().forEach(sampleId -> {
      CsvRecordHeaderValues csvRecordHeaderValues = new CsvRecordHeaderValues();
      csvRecordHeaderValues.getHeaderValueMap().put(dataMutationFileSampleIdHeader, sampleId);
      csvRecordHeaderValues.getHeaderValueMap()
          .put(idManagerPseudonymIdType, sampleIdPseudonymMap.get(sampleId));
      pivotedCsvRecordHeaderValues.addCsvRecordHeaderValues(csvRecordHeaderValues);
    });
    return pivotedCsvRecordHeaderValues;
  }


  private void executeScript(Path path) throws IOException, InterruptedException {
    ProcessBuilder processBuilder = new ProcessBuilder(scriptInterpreter, getScript(),
        path.toAbsolutePath().toString());
    Process process = processBuilder.start();
    int statusCode = process.waitFor();
    //TODO
    System.out.println();
  }

  private String getScript() {
    return getClass().getClassLoader().getResource(MtbaConst.MTBA_TRANSFORMER_SCRIPT_FILENAME)
        .toString();
  }

}
