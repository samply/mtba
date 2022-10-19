package de.samply.file.csv.updater;

import de.samply.file.bundle.PathsBundle;
import de.samply.file.csv.CsvRecordHeaderValues;
import de.samply.file.csv.reader.CsvReaderException;
import de.samply.file.csv.reader.CsvReaderImpl;
import de.samply.file.csv.reader.CsvReaderParameters;
import de.samply.utils.Constants;
import de.samply.utils.RandomPathGenerator;
import java.nio.file.Files;
import java.util.HashSet;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
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
  void deleteColumns() {

  }

  // Noch nicht
/*
    @Test
    void addPivotedCsvRecordHeaderValues() {
    }

    @Test
    void applyConsumer() {
    }
*/

  private CsvReaderParameters createCsvReaderParameters() throws IOException {

    CsvReaderParameters csvReaderParameters = new CsvReaderParameters();
    csvReaderParameters.setPathsBundle(pathsBundle);
    Path path = pathsBundle.getAllPaths().stream().collect(Collectors.toList()).get(0);
    //headers = getRandomHeaders(path);

    //csvReaderParameters.setHeaders(headers);
    csvReaderParameters.setFilename(path.getFileName().toString());

    return csvReaderParameters;

  }

  private Set<String> getRandomHeaders(Path path) throws IOException {
    HashSet<String> headers = new HashSet<>();

    boolean atLeastOneHeader = false;
    for (String header : Files.readAllLines(path).get(0).split(Constants.DEFAULT_DELIMITER)) {
      if (!atLeastOneHeader || Math.random() < 0.5) {
        headers.add(header);
        atLeastOneHeader = true;
      }
    }

    return headers;

  }

  private Set<String> getAllHeaders(Path path) throws IOException {
    HashSet<String> headers = new HashSet<>();
    for (String header : Files.readAllLines(path).get(0).split(Constants.DEFAULT_DELIMITER)) {
      headers.add(header);
    }
    return headers;
  }


}
