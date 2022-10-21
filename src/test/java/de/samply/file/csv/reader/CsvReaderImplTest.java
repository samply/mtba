package de.samply.file.csv.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.samply.file.bundle.PathsBundle;
import de.samply.file.csv.CsvRecordHeaderValues;
import de.samply.utils.Constants;
import de.samply.utils.RandomPathGenerator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class CsvReaderImplTest {

  private static final int FILES_NUMBER = 5;
  private PathsBundle pathsBundle;

  @BeforeEach
  void setUp() throws IOException {
    List<Path> randomPaths = RandomPathGenerator.createRandomCsvPaths(FILES_NUMBER);
    pathsBundle = new PathsBundle();
    pathsBundle.addPaths(randomPaths);
  }

  @AfterEach
  void tearDown() throws IOException {
    FileUtils.deleteDirectory(pathsBundle.getDirectory().toFile());
  }

  @Test
  void readCsvRecordHeaderValues() throws CsvReaderException, IOException {
    CsvReaderParameters csvReaderParameters = createCsvReaderParameters();
    readCsvRecordHeaderValues(csvReaderParameters, csvReaderParameters.getHeaders());
  }

  @Test
  void readAllCsvRecordHeaderValues() throws CsvReaderException, IOException {
    CsvReaderParameters csvReaderParameters = createCsvReaderParameters();
    csvReaderParameters.clearHeaders();
    Set<String> headersToRead = getAllHeaders(csvReaderParameters.getPath());
    readCsvRecordHeaderValues(csvReaderParameters, headersToRead);
  }

  void readCsvRecordHeaderValues(CsvReaderParameters csvReaderParameters, Set<String> headersToRead)
      throws IOException, CsvReaderException {

    int numberOfLines = getNumberOfLines(csvReaderParameters.getPath());

    CsvReader csvReader = new CsvReaderImpl(csvReaderParameters);
    AtomicInteger counter = new AtomicInteger(0);
    csvReader.readCsvRecordHeaderValues().forEach(csvRecordHeaderValues -> {
      checkCsvRecordHeaderValues(csvRecordHeaderValues, headersToRead);
      counter.incrementAndGet();
    });
    assertEquals(numberOfLines, counter.get());

  }

  private void checkCsvRecordHeaderValues(CsvRecordHeaderValues csvRecordHeaderValues, Set<String> headersToRead) {

    // TODO: Alternative: Datei manuell lesen und in String[][] konvertieren. Test: Werte vergleichen.
    for (String header : headersToRead) {
      assertNotNull(csvRecordHeaderValues.getValue(header));
    }
    assertEquals(headersToRead.size(), csvRecordHeaderValues.getHeaderValueMap().keySet().size());
  }

  private CsvReaderParameters createCsvReaderParameters() throws IOException {

    CsvReaderParameters csvReaderParameters = new CsvReaderParameters();
    csvReaderParameters.setPathsBundle(pathsBundle);
    Path path = pathsBundle.getAllPaths().stream().collect(Collectors.toList()).get(0);

    csvReaderParameters.setHeaders(getRandomHeaders(path));
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

  private int getNumberOfLines(Path path) throws IOException {
    return ((int) Files.readAllLines(path).stream().filter(line -> line.length() > 0).count()) - 1;
  }

}
