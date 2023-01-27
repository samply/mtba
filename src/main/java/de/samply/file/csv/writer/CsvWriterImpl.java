package de.samply.file.csv.writer;

import de.samply.file.csv.CsvRecordHeaderOrder;
import de.samply.file.csv.CsvRecordHeaderValues;
import de.samply.spring.MtbaConst;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVFormat.Builder;
import org.apache.commons.csv.CSVPrinter;

public class CsvWriterImpl implements CsvWriter {

  private CsvRecordHeaderOrder csvRecordHeaderOrder;
  private Path outputPath;
  private CSVPrinter csvPrinter;
  private int maxNumberOfRowsForFlush = 100;
  private int flushCounter = 0;
  private String delimiter = MtbaConst.DEFAULT_CSV_DELIMITER;
  private String endOfLine = MtbaConst.DEFAULT_END_OF_LINE;
  private Charset charset = MtbaConst.DEFAULT_CHARSET;

  /**
   * Writes csv records in csv file.
   *
   * @param csvWriterParameters csv writer parameters.
   * @throws CsvWriterException exception that encapsulates internal exceptions.
   */
  public CsvWriterImpl(CsvWriterParameters csvWriterParameters) throws CsvWriterException {

    addFileConfig(csvWriterParameters);
    this.csvRecordHeaderOrder = csvWriterParameters.getCsvRecordHeaderOrder();
    this.outputPath = generateOutputPath(csvWriterParameters);
    csvWriterParameters.getPathsBundle().addPath(outputPath);
    this.csvPrinter = createCsvPrinter(csvRecordHeaderOrder, outputPath);

  }

  private void addFileConfig(CsvWriterParameters csvWriterParameters) {
    if (csvWriterParameters.getDelimiter() != null) {
      this.delimiter = csvWriterParameters.getDelimiter();
    }
    if (csvWriterParameters.getEndOfLine() != null) {
      this.endOfLine = csvWriterParameters.getEndOfLine();
    }
    if (csvWriterParameters.getCharset() != null) {
      this.charset = csvWriterParameters.getCharset();
    }
  }

  private Path generateOutputPath(CsvWriterParameters csvWriterParameters) {

    String file = csvWriterParameters.getPathsBundle().getDirectory()
        + FileSystems.getDefault().getSeparator()
        + csvWriterParameters.getOutputFilename();

    return Paths.get(file);

  }

  /**
   * Add values to csv record.
   *
   * @param csvRecordHeaderValues values to be added.
   * @throws CsvWriterException exception that encapsulates internal exceptions.
   */
  @Override
  public void writeCsvRecord(CsvRecordHeaderValues csvRecordHeaderValues)
      throws CsvWriterException {
    try {
      addCsvRecord_WithoutManagementException(csvRecordHeaderValues);
    } catch (IOException e) {
      throw new CsvWriterException(e);
    }
  }

  private void addCsvRecord_WithoutManagementException(CsvRecordHeaderValues csvRecordHeaderValues)
      throws IOException {

    String[] values = fetchValuesInOrder(csvRecordHeaderValues);
    csvPrinter.printRecord(values);
    flush();

  }

  private void flush() throws IOException {

    flushCounter++;
    if (flushCounter >= maxNumberOfRowsForFlush) {
      csvPrinter.flush();
      flushCounter = 0;
    }

  }

  private String[] fetchValuesInOrder(CsvRecordHeaderValues csvRecordHeaderValues) {

    List<String> values = new ArrayList<>();
    for (String header : csvRecordHeaderOrder.getHeadersInOrder()) {

      String value = csvRecordHeaderValues.getValue(header);
      if (value != null) {
        values.add(value);
      } else {
        values.add("");
      }

    }

    return values.toArray(new String[0]);

  }

  private CSVPrinter createCsvPrinter(CsvRecordHeaderOrder csvRecordHeaderOrder, Path outputPath)
      throws CsvWriterException {
    try {
      return createCsvPrinter_WithoutManagementException(csvRecordHeaderOrder, outputPath);
    } catch (IOException e) {
      throw new CsvWriterException(e);
    }
  }

  private CSVPrinter createCsvPrinter_WithoutManagementException(
      CsvRecordHeaderOrder csvRecordHeaderOrder, Path outputPath)
      throws IOException {

    String[] headersInOrder = getHeadersInOrder(csvRecordHeaderOrder);
    Builder builder = Builder.create();
    if (headersInOrder.length > 0) {
      builder.setHeader(headersInOrder);
    } else {
      builder.setHeader();
    }
    CSVFormat csvFormat = builder
        .setDelimiter(delimiter)
        .setRecordSeparator(endOfLine)
        .build();
    BufferedWriter bufferedWriter = Files.newBufferedWriter(outputPath, charset);

    return new CSVPrinter(bufferedWriter, csvFormat);

  }

  private String[] getHeadersInOrder(CsvRecordHeaderOrder csvRecordHeaderOrder) {
    List<String> headersInOrder = csvRecordHeaderOrder.getHeadersInOrder();
    return headersInOrder.toArray(new String[0]);
  }

  /**
   * Get Csv Record Header Order.
   *
   * @return csv record header order.
   */
  @Override
  public CsvRecordHeaderOrder getCsvRecordHeaderOrder() {
    return csvRecordHeaderOrder;
  }

  public void setCsvRecordHeaderOrder(CsvRecordHeaderOrder csvRecordHeaderOrder)
      throws CsvWriterException {
    this.csvRecordHeaderOrder = csvRecordHeaderOrder;
    this.csvPrinter = createCsvPrinter(csvRecordHeaderOrder, outputPath);
  }

  @Override
  public void close() throws IOException {
    if (csvPrinter != null) {
      csvPrinter.flush();
      csvPrinter.close();
    }
  }


}
