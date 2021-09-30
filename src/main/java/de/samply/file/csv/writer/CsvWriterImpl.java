package de.samply.file.csv.writer;

import de.samply.file.csv.CsvRecordHeaderOrder;
import de.samply.file.csv.CsvRecordHeaderValues;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class CsvWriterImpl implements CsvWriter {

  private CsvRecordHeaderOrder csvRecordHeaderOrder;
  private Path outputPath;
  private CSVPrinter csvPrinter;
  private int maxNumberOfRowsForFlush = 100;
  private int flushCounter = 0;

  /**
   * Writes csv records in csv file.
   *
   * @param csvWriterParameters csv writer parameters.
   * @throws CsvWriterException exception that encapsulates internal exceptions.
   */
  public CsvWriterImpl(CsvWriterParameters csvWriterParameters) throws CsvWriterException {

    this.csvRecordHeaderOrder = csvWriterParameters.getCsvRecordHeaderOrder();
    this.outputPath = generateOutputPath(csvWriterParameters);
    csvWriterParameters.getPathsBundle().addPath(outputPath);
    this.csvPrinter = createCsvPrinter(csvRecordHeaderOrder, outputPath);

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

    return (String[]) values.toArray();

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
    CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(headersInOrder);
    BufferedWriter bufferedWriter = Files.newBufferedWriter(outputPath);

    return new CSVPrinter(bufferedWriter, csvFormat);

  }

  private String[] getHeadersInOrder(CsvRecordHeaderOrder csvRecordHeaderOrder) {
    List<String> headersInOrder = csvRecordHeaderOrder.getHeadersInOrder();
    return (String[]) headersInOrder.toArray();
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

  @Override
  public void close() throws IOException {
    if (csvPrinter != null) {
      csvPrinter.flush();
      csvPrinter.close();
    }
  }

}
