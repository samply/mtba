package de.samply.file.csv.writer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.samply.file.bundle.PathsBundle;
import de.samply.file.csv.CsvRecordHeaderOrder;
import de.samply.file.csv.CsvRecordHeaderValues;
import de.samply.file.csv.reader.CsvReader;
import de.samply.file.csv.reader.CsvReaderException;
import de.samply.file.csv.reader.CsvReaderImpl;
import de.samply.file.csv.reader.CsvReaderParameters;
import de.samply.spring.MtbaConst;
import de.samply.utils.RandomPathGenerator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class CsvWriterImplTest {

  private PathsBundle pathsBundle;
  private CsvWriterFactory csvWriterFactory;
  private CsvRecordHeaderOrder csvRecordHeaderOrder;
  private String outputFilename = "tempOutput";

  @BeforeEach
  void setUp() throws IOException {

    List<Path> randomPaths = RandomPathGenerator.createRandomCsvPaths(1);
    pathsBundle = new PathsBundle();
    pathsBundle.addPaths(randomPaths);
    csvRecordHeaderOrder = invertOrderAndSupressRandomlyHeaders(randomPaths.get(0));
    csvWriterFactory = new CsvWriterFactoryImpl(pathsBundle);

  }

  @AfterEach
  void tearDown() throws IOException {
    FileUtils.deleteDirectory(pathsBundle.getDirectory().toFile());
  }

  @Test
  void writeCsvRecord() throws CsvWriterFactoryException, IOException, CsvReaderException {

    CsvReaderParameters csvReaderParameters = createCsvReaderParameters();
    try (CsvReader csvReader = new CsvReaderImpl(csvReaderParameters);
        CsvWriter csvWriter = csvWriterFactory.create(csvRecordHeaderOrder, outputFilename)) {

      csvReader.readCsvRecordHeaderValues().forEach(csvRecordHeaderValues -> {
        try {
          csvWriter.writeCsvRecord(csvRecordHeaderValues);
        } catch (CsvWriterException e) {
          e.printStackTrace();
        }
      });

    }

    pathsBundle.addPath(Paths.get(outputFilename));
    CsvReaderParameters csvReaderParameters2 = new CsvReaderParameters(outputFilename, pathsBundle);

    try (CsvReader csvReader2 = new CsvReaderImpl(csvReaderParameters);
        CsvReader csvReader3 = new CsvReaderImpl(csvReaderParameters2)) {

      Iterator<CsvRecordHeaderValues> writerIterator = csvReader3.readCsvRecordHeaderValues()
          .iterator();
      Iterator<CsvRecordHeaderValues> readerIterator = csvReader2.readCsvRecordHeaderValues()
          .iterator();

      while (writerIterator.hasNext()) {
        CsvRecordHeaderValues writerValues = writerIterator.next();
        CsvRecordHeaderValues readerValues = readerIterator.next();

        for (String header : writerValues.getHeaderValueMap().keySet()) {
          assertEquals(writerValues.getValue(header), readerValues.getValue(header));
        }

      }
    }


  }

  private CsvRecordHeaderOrder invertOrderAndSupressRandomlyHeaders(Path path) throws IOException {

    CsvRecordHeaderOrder csvRecordHeaderOrder = new CsvRecordHeaderOrder();

    String[] headers = Files.readAllLines(path).get(0).split(MtbaConst.DEFAULT_CSV_DELIMITER);

    boolean atLeastOne = false;
    int order = 0;
    for (int i = headers.length - 1; i >= 0; i--) {
      if (!atLeastOne || Math.random() < 0.5) {
        csvRecordHeaderOrder.addHeader(headers[i], order++);
        atLeastOne = true;
      }
    }

    return csvRecordHeaderOrder;

  }

  private CsvReaderParameters createCsvReaderParameters() throws IOException {

    CsvReaderParameters csvReaderParameters = new CsvReaderParameters();

    csvReaderParameters.setPathsBundle(pathsBundle);
    Path path = pathsBundle.getAllPaths().stream().collect(Collectors.toList()).get(0);

    csvReaderParameters.setFilename(path.getFileName().toString());

    return csvReaderParameters;

  }


}
