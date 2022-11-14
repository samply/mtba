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
import de.samply.file.csv.updater.PivotedCsvRecordHeaderValues;
import de.samply.spring.MtbaConst;
import de.samply.utils.PathsBundleUtils;
import de.samply.utils.TemporalDirectoryManager;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    // Add pseudonym
    addPseudonymToDataMutationFile(pathsBundle);
    // Execute script
    PathsBundle outputPathsBundle = executeScriptAndGetResults(
        pathsBundle.getPath(dataMutationFile));
    PathsBundleUtils.addPathsBundleAsVariable(delegateExecution, outputPathsBundle);
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


  private PathsBundle executeScriptAndGetResults(Path path)
      throws IOException, InterruptedException {
    String outputFilename = createOutputFilename(path);
    PathsBundle outputPathsBundle = createOutputPathsBundle(outputFilename);
    Path scriptPath = copyScriptToTemporalPath();
    ProcessBuilder processBuilder = new ProcessBuilder(scriptInterpreter,
        scriptPath.toAbsolutePath().toString(),
        path.toAbsolutePath().toString(),
        outputPathsBundle.getPath(outputFilename).toAbsolutePath().toString());
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
        new InputStreamReader(process.getInputStream()))) {
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
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
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
    try (FileWriter fileWriter = new FileWriter(scriptPath.toFile())) {
      String line;
      while ((line = reader.readLine()) != null) {
        fileWriter.write(line);
        fileWriter.write('\n');
      }
    }
  }

  private void deleteScriptPath(Path scriptPath) throws IOException {
    Files.delete(scriptPath);
    Files.delete(scriptPath.getParent());
  }

}
