package de.samply.file.csv.updater;

import de.samply.file.bundle.PathsBundle;
import de.samply.file.csv.CsvRecordHeaderOrder;
import de.samply.file.csv.reader.CsvReaderParameters;
import de.samply.file.csv.writer.CsvWriterException;
import de.samply.file.csv.writer.CsvWriterParameters;
import de.samply.spring.MtbaConst;
import de.samply.utils.FileConfigUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.csv.CSVFormat.Builder;
import org.apache.commons.csv.CSVParser;

public class CsvUpdaterFactoryImpl implements CsvUpdaterFactory {

  public static final String CSV_UPDATE_FILENAME_EXTENSION = "-updating.csv";
  private int maxNumberOfRowsPerFlush = 100;
  private String delimiter = MtbaConst.DEFAULT_CSV_DELIMITER;
  private String endOfLine = MtbaConst.DEFAULT_END_OF_LINE;
  private Charset charset = MtbaConst.DEFAULT_CHARSET;


  public CsvUpdaterFactoryImpl() {
  }

  public CsvUpdaterFactoryImpl(int maxNumberOfRowsPerFlush) {
    this.maxNumberOfRowsPerFlush = maxNumberOfRowsPerFlush;
  }

  /**
   * Create csv reader updater for csv reader parameters.
   *
   * @param csvReaderParameters csv reader parameters.
   * @return csv updater.
   * @throws CsvUpdaterFactoryException exception that encapsulates internal exceptions.
   */
  @Override
  public CsvUpdater createCsvUpdater(CsvReaderParameters csvReaderParameters)
      throws CsvUpdaterFactoryException {

    CsvWriterParameters csvWriterParameters = createCsvWriterParameters(csvReaderParameters);

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

    csvUpdaterParameters.setCharset(this.charset);
    csvUpdaterParameters.setDelimiter(this.delimiter);
    csvUpdaterParameters.setEndOfLine(this.endOfLine);

    return new CsvUpdaterImpl(csvUpdaterParameters);

  }

  private CsvWriterParameters createCsvWriterParameters(CsvReaderParameters csvReaderParameters)
      throws CsvUpdaterFactoryException {

    CsvWriterParameters csvWriterParameters = new CsvWriterParameters();

    PathsBundle pathsBundle = csvReaderParameters.getPathsBundle();
    String outputFilename = extractOutputFilename(csvReaderParameters);
    CsvRecordHeaderOrder csvRecordHeaderOrder = extractCsvRecordHeaderOrder(
        csvReaderParameters.getPath());

    csvWriterParameters.setPathsBundle(pathsBundle);
    csvWriterParameters.setOutputFilename(outputFilename);
    csvWriterParameters.setMaxNumberOfRowsForFlush(maxNumberOfRowsPerFlush);
    csvWriterParameters.setCsvRecordHeaderOrder(csvRecordHeaderOrder);

    csvWriterParameters.setDelimiter(delimiter);
    csvWriterParameters.setCharset(charset);
    csvWriterParameters.setEndOfLine(endOfLine);

    return csvWriterParameters;

  }

  private String extractOutputFilename(CsvReaderParameters csvReaderParameters) {
    return csvReaderParameters.getFilename() + CSV_UPDATE_FILENAME_EXTENSION;
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

    BufferedReader bufferedReader = Files.newBufferedReader(inputPath, charset);
    try (CSVParser csvParser = Builder
        .create()
        .setHeader()
        .setSkipHeaderRecord(true)
        .setDelimiter(delimiter)
        .setRecordSeparator(endOfLine)
        .build()
        .parse(bufferedReader)) {
      int counter = 0;
      for (String header : csvParser.getHeaderNames()) {
        csvRecordHeaderOrder.addHeader(header, counter++);
      }
    }

    return csvRecordHeaderOrder;

  }

  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  public void setEndOfLine(String endOfLine) {
    this.endOfLine = endOfLine;
  }

  public void setCharset(Charset charset) {
    this.charset = charset;
  }

}
