package de.samply.file.csv.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.samply.file.bundle.PathsBundle;
import de.samply.file.csv.CsvRecordHeaderValues;
import de.samply.file.csv.TestUtils;
import de.samply.utils.RandomPathGenerator;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
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
    TestUtils.deleteDirectory(pathsBundle);
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
    Set<String> headersToRead = TestUtils.getAllHeaders(csvReaderParameters.getPath());
    readCsvRecordHeaderValues(csvReaderParameters, headersToRead);
  }

  void readCsvRecordHeaderValues(CsvReaderParameters csvReaderParameters, Set<String> headersToRead)
      throws IOException, CsvReaderException {

    int numberOfLines = TestUtils.getNumberOfLines(csvReaderParameters.getPath());
    List<Map<String, String>> pathValues = TestUtils.fetchFileValues(csvReaderParameters.getPath());

    CsvReader csvReader = new CsvReaderImpl(csvReaderParameters);
    AtomicInteger counter = new AtomicInteger(0);
    csvReader.readCsvRecordHeaderValues().forEach(csvRecordHeaderValues ->
        checkCsvRecordHeaderValues(csvRecordHeaderValues, headersToRead, counter.getAndIncrement(),
            pathValues));
    assertEquals(numberOfLines, counter.get());

  }

  private void checkCsvRecordHeaderValues(CsvRecordHeaderValues csvRecordHeaderValues,
      Set<String> headersToRead, int counter, List<Map<String, String>> pathValues) {
    for (String header : headersToRead) {
      assertEquals(pathValues.get(counter).get(header), csvRecordHeaderValues.getValue(header));
    }
    assertEquals(headersToRead.size(), csvRecordHeaderValues.getHeaderValueMap().keySet().size());
  }

  private CsvReaderParameters createCsvReaderParameters() throws IOException {

    CsvReaderParameters csvReaderParameters = new CsvReaderParameters();
    csvReaderParameters.setPathsBundle(pathsBundle);
    Path path = pathsBundle.getAllPaths().stream().collect(Collectors.toList()).get(0);

    csvReaderParameters.setHeaders(TestUtils.getRandomHeaders(path));
    csvReaderParameters.setFilename(path.getFileName().toString());

    return csvReaderParameters;

  }


}
