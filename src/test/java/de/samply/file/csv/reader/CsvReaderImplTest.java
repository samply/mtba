package de.samply.file.csv.reader;

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
  private Set<String> headers;

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

    CsvReader csvReader = new CsvReaderImpl(csvReaderParameters);
    csvReader.readCsvRecordHeaderValues()
        .forEach(csvRecordHeaderValues -> checkCsvRecordHeaderValues(csvRecordHeaderValues));

  }

  private void checkCsvRecordHeaderValues(CsvRecordHeaderValues csvRecordHeaderValues) {
    for (String header : headers) {
      assertNotNull(csvRecordHeaderValues.getValue(header));
    }
  }

  private CsvReaderParameters createCsvReaderParameters() throws IOException {

    CsvReaderParameters csvReaderParameters = new CsvReaderParameters();

    csvReaderParameters.setPathsBundle(pathsBundle);
    Path path = pathsBundle.getAllPaths().stream().collect(Collectors.toList()).get(0);
    headers = getRandomHeaders(path);

    csvReaderParameters.setHeaders(headers);
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

}
