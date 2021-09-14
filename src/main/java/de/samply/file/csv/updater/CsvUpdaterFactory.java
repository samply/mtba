package de.samply.file.csv.updater;

import de.samply.file.bundle.PathsBundle;
import de.samply.file.csv.CsvRecordHeaderOrder;
import de.samply.file.csv.reader.CsvReaderParameters;
import de.samply.file.csv.writer.CsvWriterException;
import de.samply.file.csv.writer.CsvWriterParameters;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

public class CsvUpdaterFactory {

  public static final String CSV_UPDATE_FILENAME_EXTENSION = "-updating.csv";
  private int maxNumberOfRowsPerFlush = 100;


  public CsvUpdaterFactory(int maxNumberOfRowsPerFlush) {
    this.maxNumberOfRowsPerFlush = maxNumberOfRowsPerFlush;
  }

  /**
   * Create csv updater from input path.
   *
   * @param inputPath input path.
   * @return csv updater.
   */
  public CsvUpdater createCsvUpdater(Path inputPath) throws CsvUpdaterFactoryException {

    CsvReaderParameters csvReaderParameters = createCsvReaderParameters(inputPath);
    CsvWriterParameters csvWriterParameters = createCsvWriterParameters(inputPath);

    return createCsvUpdater(csvReaderParameters, csvWriterParameters);

  }

  private CsvUpdater createCsvUpdater(CsvReaderParameters csvReaderParameters,
      CsvWriterParameters csvWriterParameters) throws CsvUpdaterFactoryException {
    try {
      return createCsvUpdater_WithoutManagementException(csvReaderParameters, csvWriterParameters);
    } catch (CsvWriterException e) {
      throw new CsvUpdaterFactoryException(e);
    }
  }

  private CsvUpdater createCsvUpdater_WithoutManagementException(
      CsvReaderParameters csvReaderParameters,
      CsvWriterParameters csvWriterParameters) throws CsvWriterException {

    CsvUpdaterParameters csvUpdaterParameters = new CsvUpdaterParameters();
    csvUpdaterParameters.setCsvReaderParameters(csvReaderParameters);
    csvUpdaterParameters.setCsvWriterParameters(csvWriterParameters);

    return new CsvUpdater(csvUpdaterParameters);

  }

  private CsvReaderParameters createCsvReaderParameters(Path inputPath) {

    CsvReaderParameters csvReaderParameters = new CsvReaderParameters();

    String inputFilename = getInputFilename(inputPath);
    PathsBundle pathsBundle = createpPathsBundle(inputPath);

    csvReaderParameters.setFilename(inputFilename);
    csvReaderParameters.setPathsBundle(pathsBundle);

    return csvReaderParameters;

  }

  private PathsBundle createpPathsBundle(Path inputPath) {

    PathsBundle pathsBundle = new PathsBundle();
    pathsBundle.addPath(inputPath);

    return pathsBundle;

  }


  private CsvWriterParameters createCsvWriterParameters(Path inputPath)
      throws CsvUpdaterFactoryException {

    CsvWriterParameters csvWriterParameters = new CsvWriterParameters();

    String outputFolderPath = extractOutputFolderPath(inputPath);
    String outputFilename = extractOutputFilename(inputPath);
    CsvRecordHeaderOrder csvRecordHeaderOrder = extractCsvRecordHeaderOrder(inputPath);

    csvWriterParameters.setOutputFolderPath(outputFolderPath);
    csvWriterParameters.setOutputFilename(outputFilename);
    csvWriterParameters.setMaxNumberOfRowsForFlush(maxNumberOfRowsPerFlush);
    csvWriterParameters.setCsvRecordHeaderOrder(csvRecordHeaderOrder);

    return csvWriterParameters;

  }

  private String extractOutputFolderPath(Path inputPath) {
    return inputPath.getParent().toString();
  }

  private String extractOutputFilename(Path inputPath) {
    return getInputFilename(inputPath) + CSV_UPDATE_FILENAME_EXTENSION;
  }

  private String getInputFilename(Path inputPath) {
    return inputPath.getFileName().toString();
  }

  private CsvRecordHeaderOrder extractCsvRecordHeaderOrder(Path inputPath)
      throws CsvUpdaterFactoryException {
    try {
      return extractCsvRecordHeaderOrder_WithoutManagementException(inputPath);
    } catch (IOException e) {
      throw new CsvUpdaterFactoryException(e);
    }
  }

  private CsvRecordHeaderOrder extractCsvRecordHeaderOrder_WithoutManagementException(
      Path inputPath)
      throws IOException {

    CsvRecordHeaderOrder csvRecordHeaderOrder = new CsvRecordHeaderOrder();

    BufferedReader bufferedReader = Files.newBufferedReader(inputPath);
    CSVParser csvParser = CSVFormat.DEFAULT.parse(bufferedReader);
    int counter = 0;
    for (String header : csvParser.getHeaderNames()) {
      csvRecordHeaderOrder.addHeader(header, counter++);
    }

    return csvRecordHeaderOrder;

  }

}
