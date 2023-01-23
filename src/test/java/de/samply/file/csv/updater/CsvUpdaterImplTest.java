package de.samply.file.csv.updater;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.samply.file.bundle.PathsBundle;
import de.samply.file.csv.CsvRecordHeaderValues;
import de.samply.file.csv.reader.CsvReaderException;
import de.samply.file.csv.reader.CsvReaderImpl;
import de.samply.file.csv.reader.CsvReaderParameters;
import de.samply.spring.MtbaConst;
import de.samply.utils.RandomPathGenerator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class CsvUpdaterImplTest {

  private CsvUpdaterFactoryImpl csvUpdaterFactory = new CsvUpdaterFactoryImpl();
  private CsvUpdater csvUpdater;
  CsvReaderParameters csvReaderParameters;
  private static final int FILES_NUMBER = 5;
  private PathsBundle pathsBundle;
  private Set<String> headers;
  private final static String NEW_HEADER_SUFFIX = "CHANGED";

  @BeforeEach
  void setUp() throws CsvUpdaterFactoryException, IOException, CsvUpdaterException {
    List<Path> randomPaths = RandomPathGenerator.createRandomCsvPaths(FILES_NUMBER);
    pathsBundle = new PathsBundle();
    pathsBundle.addPaths(randomPaths);

    csvReaderParameters = createCsvReaderParameters();
    csvUpdater = csvUpdaterFactory.createCsvUpdater(csvReaderParameters);
  }

  @AfterEach
  void tearDown() throws IOException {
    FileUtils.deleteDirectory(pathsBundle.getDirectory().toFile());
  }

  @Test
  void renameColumns() throws CsvUpdaterException, CsvReaderException, IOException {

    Map<String, String> oldHeadersNewHeadersMap = new HashMap<>();
    getRandomHeaders(csvReaderParameters.getPath()).stream().forEach(
        oldHeader -> oldHeadersNewHeadersMap.put(oldHeader, oldHeader + NEW_HEADER_SUFFIX));
    csvUpdater.renameColumns(oldHeadersNewHeadersMap);

    CsvReaderImpl csvReader = new CsvReaderImpl(csvReaderParameters);
    csvReader.readCsvRecordHeaderValues().forEach(
        csvRecordHeaderValues -> checkRename(csvRecordHeaderValues, oldHeadersNewHeadersMap));

  }

  private void checkRename(CsvRecordHeaderValues csvRecordHeaderValues,
      Map<String, String> oldHeadersNewHeadersMap) {
    try {
      getAllHeaders(csvReaderParameters.getPath()).forEach(
          header -> {
            String newHeader = oldHeadersNewHeadersMap.get(header);
            assertNotNull(csvRecordHeaderValues.getValue((newHeader != null) ? newHeader : header));
          });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  @Test
  void deleteColumns() throws IOException, CsvUpdaterException, CsvReaderException {
    Set<String> randomHeaders = getRandomHeaders(csvReaderParameters.getPath());
    int oldNumberOfLines = getNumberOfLines(csvReaderParameters.getPath());
    csvUpdater.deleteColumns(randomHeaders);

    CsvReaderImpl csvReader = new CsvReaderImpl(csvReaderParameters);
    csvReader.readCsvRecordHeaderValues().forEach(
        csvRecordHeaderValues -> checkDeleteColumns(csvRecordHeaderValues, randomHeaders));

    Set<String> newHeaders = getAllHeaders(csvReaderParameters.getPath());
    int newNumberOfLines = getNumberOfLines(csvReaderParameters.getPath());

    assertEquals(newNumberOfLines, oldNumberOfLines);
    newHeaders.forEach(header -> assertTrue(!randomHeaders.contains(header)));

  }

  private void checkDeleteColumns(CsvRecordHeaderValues csvRecordHeaderValues,
      Set<String> removedHeaders) {
    csvRecordHeaderValues.getHeaderValueMap().keySet()
        .forEach(header -> assertTrue(!removedHeaders.contains(header)));
  }

  @Test
  void addPivotedCsvRecordHeaderValues()
      throws CsvUpdaterException, CsvReaderException, IOException {

    PivotedCsvRecordHeaderValues pivotedCsvRecordHeaderValues = createPivotedCsvRecordHeaderValues();
    csvUpdater.addPivotedCsvRecordHeaderValues(pivotedCsvRecordHeaderValues);

    CsvReaderImpl csvReader = new CsvReaderImpl(csvReaderParameters);
    csvReader.readCsvRecordHeaderValues().forEach(csvRecordHeaderValues -> {
      String pivotedValue = csvRecordHeaderValues.getValue(
          pivotedCsvRecordHeaderValues.getPivotHeader());
      assertNotNull(pivotedValue);
      pivotedCsvRecordHeaderValues.getCsvRecordHeaderValues(pivotedValue).getHeaderValueMap()
          .keySet().forEach(header -> {
            assertEquals(
                pivotedCsvRecordHeaderValues.getCsvRecordHeaderValues(pivotedValue).getValue(header),
                csvRecordHeaderValues.getValue(header));
          });
    });

  }

  private PivotedCsvRecordHeaderValues createPivotedCsvRecordHeaderValues()
      throws CsvReaderException, IOException {

    String[] headers = getAllHeaders(csvReaderParameters.getPath()).toArray(String[]::new);
    String pivotHeader = headers[0];
    int currentNumberOfHeaders = headers.length;
    int numberOfAdditionalHeaders = RandomPathGenerator.generateRandomNumber();

    PivotedCsvRecordHeaderValues pivotedCsvRecordHeaderValues = new PivotedCsvRecordHeaderValues(
        pivotHeader);

    CsvReaderImpl csvReader = new CsvReaderImpl(csvReaderParameters);
    csvReader.readCsvRecordHeaderValues().forEach(
        csvRecordHeaderValues -> {
          String value = csvRecordHeaderValues.getValue(pivotHeader);
          CsvRecordHeaderValues newCsvRecordHeaderValues = createCsvRecordHeaderValues(
              currentNumberOfHeaders, numberOfAdditionalHeaders);
          newCsvRecordHeaderValues.getHeaderValueMap().put(pivotHeader, value);
          pivotedCsvRecordHeaderValues.addCsvRecordHeaderValues(newCsvRecordHeaderValues);
        });

    return pivotedCsvRecordHeaderValues;
  }

  private CsvRecordHeaderValues createCsvRecordHeaderValues(int currentNumberOfHeaders,
      int numberOfAdditionalHeaders) {

    Set<String> headers = createHeaders(currentNumberOfHeaders, numberOfAdditionalHeaders);
    CsvRecordHeaderValues csvRecordHeaderValues = new CsvRecordHeaderValues();

    headers.forEach(header ->
        Arrays.stream(RandomPathGenerator.
                generateRandomContent(1, numberOfAdditionalHeaders)
                .get(0)
                .split(MtbaConst.DEFAULT_CSV_DELIMITER))
            .forEach(value -> csvRecordHeaderValues.getHeaderValueMap().put(header, value)));

    return csvRecordHeaderValues;

  }

  private Set<String> createHeaders(int initialCounter, int numberOfHeaders) {
    return Set.of(RandomPathGenerator.generateHeaders(initialCounter, numberOfHeaders)
        .split(MtbaConst.DEFAULT_CSV_DELIMITER));
  }

  @Test
  void applyConsumer() {

  }

  private CsvReaderParameters createCsvReaderParameters() throws IOException {

    CsvReaderParameters csvReaderParameters = new CsvReaderParameters();
    csvReaderParameters.setPathsBundle(pathsBundle);
    Path path = pathsBundle.getAllPaths().stream().collect(Collectors.toList()).get(0);

    csvReaderParameters.setFilename(path.getFileName().toString());

    return csvReaderParameters;

  }

  private Set<String> getRandomHeaders(Path path) throws IOException {
    HashSet<String> headers = new HashSet<>();

    boolean atLeastOneHeader = false;
    for (String header : Files.readAllLines(path).get(0).split(MtbaConst.DEFAULT_CSV_DELIMITER)) {
      if (!atLeastOneHeader || Math.random() < 0.5) {
        headers.add(header);
        atLeastOneHeader = true;
      }
    }

    return headers;

  }

  private Set<String> getAllHeaders(Path path) throws IOException {
    HashSet<String> headers = new HashSet<>();
    for (String header : Files.readAllLines(path).get(0).split(MtbaConst.DEFAULT_CSV_DELIMITER)) {
      headers.add(header);
    }
    return headers;
  }

  private int getNumberOfLines(Path path) throws IOException {
    return ((int) Files.readAllLines(path).stream().filter(line -> line.length() > 0).count()) - 1;
  }


}
