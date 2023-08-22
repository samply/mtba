package de.samply.tasks;

import de.samply.blaze.BlazeResponse;
import de.samply.blaze.Response;
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
import de.samply.file.csv.updater.PivotedCsvRecordHeaderValues;
import de.samply.spring.MtbaConst;
import de.samply.utils.FileConfig;
import de.samply.utils.FileConfigUtils;
import de.samply.utils.PathsBundleUtils;
import de.samply.utils.TemporalDirectoryManager;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class FhirConverterDelegate implements JavaDelegate {

  private final Logger logger = LoggerFactory.getLogger(FhirConverterDelegate.class);
  private final CsvUpdaterFactory csvUpdaterFactory = new CsvUpdaterFactoryImpl();
  private TemporalDirectoryManager temporalDirectoryManager;
  private FileConfig fileConfig;
  private String dataMutationFile;
  private String scriptInterpreter;
  private String patientFilename;
  private String patientFilePatientIdHeader;
  private String idManagerPseudonymIdType;
  private String dataMutationFileSampleIdHeader;
  private String sampleFilename;
  private String sampleFileSampleIdHeader;
  private String sampleFilePatientIdHeader;
  private String blazeIdHeader;

  private WebClient blazeWebClient;

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
      @Value(MtbaConst.BLAZE_STORE_URL_SV) String blazeStoreUrl,
      @Value(MtbaConst.BLAZE_ID_HEADER_SV) String blazeIdHeader,
      @Autowired TemporalDirectoryManager temporalDirectoryManager,
      @Autowired FileConfig fileConfig
  ) {
    FileConfigUtils.addFileConfig(fileConfig, (CsvUpdaterFactoryImpl) csvUpdaterFactory);
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
    this.blazeIdHeader = blazeIdHeader;
    this.blazeWebClient = createBlazeWebClient(blazeStoreUrl);
    this.fileConfig = fileConfig;
  }


  @Override
  public void execute(DelegateExecution delegateExecution) throws Exception {
    logger.info("Convert to FHIR");
    PathsBundle pathsBundle = PathsBundleUtils.getPathsBundleVariable(delegateExecution);
    // Copy files in new temporal directory
    PathsBundleManager pathsBundleManager = new PathsBundleManagerImpl(pathsBundle.getDirectory());
    pathsBundle = pathsBundleManager.copyPathsBundleToOutputFolder(pathsBundle,
        temporalDirectoryManager.createTemporalDirectory());
    // Add pseudonym
    List<String> pseudonyms = addPseudonymToDataMutationFileAndGetPseudonymList(pathsBundle);
    // Add Blaze Id
    addBlazeIdToDataMutationFile(pathsBundle, pseudonyms);
    // Execute script
    PathsBundle outputPathsBundle = executeScriptAndGetResults(
        pathsBundle.getPath(dataMutationFile));
    PathsBundleUtils.addPathsBundleAsVariable(delegateExecution, outputPathsBundle);
  }

  private Map<String, String> fetchPseudonymBlazeIdMap(List<String> pseudonyms) {
    Map<String, String> pseudonymBlazeIdMap = new HashMap<>();
    pseudonyms.forEach(pseudonym -> {
      ResponseEntity<BlazeResponse> blazeResponseEntity = this.blazeWebClient.get().uri(
          MtbaConst.BLAZE_STORE_API_PATH + MtbaConst.BLAZE_STORE_GET_PATIENT_THROUGH_PSEUDONYM_PATH
              + pseudonym).retrieve().toEntity(BlazeResponse.class).block();
      if (blazeResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
        Response[] responses = blazeResponseEntity.getBody().getEntry();
        if (responses.length >= 1) {
          String fullUrl = responses[0].getFullUrl();
          if (fullUrl != null) {
            pseudonymBlazeIdMap.put(pseudonym, getBlazeIdFromFullUrl(fullUrl));
          }
        } else {
          //TODO
        }

      } else {
        //TODO: Handle HTTP Errors
      }
    });

    return pseudonymBlazeIdMap;
  }

  private String getBlazeIdFromFullUrl(String fullUrl) {
    return fullUrl.substring(fullUrl.lastIndexOf('/') + 1);
  }

  private void addBlazeIdToDataMutationFile(PathsBundle pathsBundle, List<String> pseudonyms)
      throws CsvUpdaterFactoryException, CsvUpdaterException {
    Map<String, String> pseudonymBlazeIdMap = fetchPseudonymBlazeIdMap(pseudonyms);
    addBlazeIdToDataMutationFile(pathsBundle, pseudonymBlazeIdMap);
  }

  private List<String> addPseudonymToDataMutationFileAndGetPseudonymList(PathsBundle pathsBundle)
      throws CsvReaderException, CsvUpdaterFactoryException, CsvUpdaterException {
    Map<String, String> patientIdPseudonymMap = fetchPatientIdPseudonymMap(pathsBundle);
    Map<String, String> sampleIdPatientIdMap = fetchSampleIdPatientIdMap(pathsBundle);
    Map<String, String> sampleIdPseudonymMap = convertToSampleIdPseudonymMap(patientIdPseudonymMap,
        sampleIdPatientIdMap);
    addPseudonymToDataMutationFile(pathsBundle, sampleIdPseudonymMap);
    return new ArrayList<>(sampleIdPseudonymMap.values());
  }

  private Map<String, String> fetchPatientIdPseudonymMap(PathsBundle pathsBundle)
      throws CsvReaderException {
    Map<String, String> patientIdPseudonymMap = new HashMap<>();
    CsvReaderParameters csvReaderParameters = new CsvReaderParameters(patientFilename, pathsBundle);
    FileConfigUtils.addFileConfig(fileConfig, csvReaderParameters);
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
    FileConfigUtils.addFileConfig(fileConfig, csvReaderParameters);
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
    CsvReaderParameters csvReaderParameters = new CsvReaderParameters(dataMutationFile,
        pathsBundle);
    FileConfigUtils.addFileConfig(fileConfig, csvReaderParameters);
    CsvUpdater csvUpdater = csvUpdaterFactory.createCsvUpdater(csvReaderParameters);
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

  private void addBlazeIdToDataMutationFile(PathsBundle pathsBundle,
      Map<String, String> pseudonymBlazeIdMap)
      throws CsvUpdaterFactoryException, CsvUpdaterException {
    CsvReaderParameters csvReaderParameters = new CsvReaderParameters(dataMutationFile,
        pathsBundle);
    FileConfigUtils.addFileConfig(fileConfig, csvReaderParameters);
    CsvUpdater csvUpdater = csvUpdaterFactory.createCsvUpdater(csvReaderParameters);
    csvUpdater.addPivotedCsvRecordHeaderValues(
        createPivotedCsvRecordHeaderValuesToAddBlazeIdToDataMutationFile(pseudonymBlazeIdMap));
  }

  private PivotedCsvRecordHeaderValues createPivotedCsvRecordHeaderValuesToAddBlazeIdToDataMutationFile(
      Map<String, String> pseudonymBlazeIdMap) {
    PivotedCsvRecordHeaderValues pivotedCsvRecordHeaderValues = new PivotedCsvRecordHeaderValues(
        idManagerPseudonymIdType);
    pseudonymBlazeIdMap.keySet().forEach(pseudonym -> {
      CsvRecordHeaderValues csvRecordHeaderValues = new CsvRecordHeaderValues();
      csvRecordHeaderValues.getHeaderValueMap().put(idManagerPseudonymIdType, pseudonym);
      csvRecordHeaderValues.getHeaderValueMap()
          .put(blazeIdHeader, pseudonymBlazeIdMap.get(pseudonym));
      pivotedCsvRecordHeaderValues.addCsvRecordHeaderValues(csvRecordHeaderValues);
    });
    return pivotedCsvRecordHeaderValues;
  }


  private PathsBundle executeScriptAndGetResults(Path path)
      throws IOException, InterruptedException {
    String outputFilename = createOutputFilename(path);
    PathsBundle outputPathsBundle = createOutputPathsBundle(outputFilename);
    Path scriptPath = copyScriptToTemporalPath();
    ProcessBuilder processBuilder = new ProcessBuilder(scriptInterpreter,
        scriptPath.toAbsolutePath().toString(),
        path.toAbsolutePath().toString(),
        outputPathsBundle.getPath(outputFilename).toAbsolutePath().toString());
    processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
    processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
    Process process = processBuilder.start();
    logProcess(process);
    int statusCode = process.waitFor();
    deleteScriptPath(scriptPath);
    if (statusCode == 0) {
      return outputPathsBundle;
    } else {
      //TODO
      return new PathsBundle();
    }
  }

  private void logProcess(Process process) throws IOException {
    try (var reader = new BufferedReader(
        new InputStreamReader(process.getInputStream(), fileConfig.getFileCharset()))) {
      String line = null;
      while ((line = reader.readLine()) != null) {
        logger.debug(line);
      }
    }
  }

  PathsBundle createOutputPathsBundle(String outputFilename) {
    Path temporalDirectory = temporalDirectoryManager.createTemporalDirectory();
    Path outputPath = temporalDirectory.resolve(outputFilename);
    PathsBundle pathsBundle = new PathsBundle();
    pathsBundle.addPath(outputPath);

    return pathsBundle;
  }

  private String createOutputFilename(Path inputPath) {
    String inputFilename = inputPath.getFileName().toString();
    return inputFilename.substring(0, inputFilename.lastIndexOf('.') + 1) + "xml";
  }

  private String getScript() {
    return getClass().getClassLoader().getResource(MtbaConst.MTBA_TRANSFORMER_SCRIPT_FILENAME)
        .getPath().substring(1);
  }

  private Path copyScriptToTemporalPath() throws IOException {
    try (InputStream inputStream = getClass().getClassLoader()
        .getResourceAsStream(MtbaConst.MTBA_TRANSFORMER_SCRIPT_FILENAME);
        InputStreamReader streamReader = new InputStreamReader(inputStream,
            fileConfig.getFileCharset());
        BufferedReader reader = new BufferedReader(streamReader)) {
      return copyScriptToTemporalPath(reader);
    }
  }

  private Path copyScriptToTemporalPath(BufferedReader reader) throws IOException {
    Path temporalDirectory = temporalDirectoryManager.createTemporalDirectory();
    Path scriptPath = temporalDirectory.resolve(MtbaConst.MTBA_TRANSFORMER_SCRIPT_FILENAME);
    writeTemporalPath(reader, scriptPath);
    return scriptPath;
  }

  private void writeTemporalPath(BufferedReader reader, Path scriptPath) throws IOException {
    try (FileWriter fileWriter = new FileWriter(scriptPath.toFile(), fileConfig.getFileCharset())) {
      String line;
      while ((line = reader.readLine()) != null) {
        fileWriter.write(line);
        fileWriter.write(fileConfig.getEndOfLine());
      }
    }
  }

  private void deleteScriptPath(Path scriptPath) throws IOException {
    Files.delete(scriptPath);
    Files.delete(scriptPath.getParent());
  }

  private WebClient createBlazeWebClient(String blazeStoreUrl) {
    //TODO: Set proxy
    return WebClient.builder().baseUrl(blazeStoreUrl)
        .defaultHeaders(httpHeaders -> {
          httpHeaders.set(HttpHeaders.CONTENT_TYPE, "application/xml+fhir");
          //TODO
        }).build();
  }

}
