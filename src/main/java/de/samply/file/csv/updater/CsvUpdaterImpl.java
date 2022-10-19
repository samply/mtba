package de.samply.file.csv.updater;

import de.samply.file.csv.CsvRecordHeaderOrder;
import de.samply.file.csv.CsvRecordHeaderValues;
import de.samply.file.csv.reader.CsvReader;
import de.samply.file.csv.reader.CsvReaderException;
import de.samply.file.csv.reader.CsvReaderImpl;
import de.samply.file.csv.reader.CsvReaderParameters;
import de.samply.file.csv.writer.CsvWriter;
import de.samply.file.csv.writer.CsvWriterException;
import de.samply.file.csv.writer.CsvWriterFactory;
import de.samply.file.csv.writer.CsvWriterFactoryException;
import de.samply.file.csv.writer.CsvWriterFactoryImpl;
import de.samply.file.csv.writer.CsvWriterParameters;
import de.samply.utils.EitherUtils;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvUpdaterImpl implements CsvUpdater {

  private final Logger logger = LoggerFactory.getLogger(CsvUpdaterImpl.class);

  private CsvReaderParameters csvReaderParameters;
  private CsvWriterParameters csvWriterParameters;
  private CsvWriterFactory csvWriterFactory;

  /**
   * Updates a csv file with new data.
   *
   * @param csvUpdaterParameters csv updater parameters.
   * @throws CsvWriterException exception that encapsulates internal exceptions.
   */
  public CsvUpdaterImpl(CsvUpdaterParameters csvUpdaterParameters) throws CsvWriterException {
    this.csvReaderParameters = csvUpdaterParameters.getCsvReaderParameters();
    this.csvWriterParameters = csvUpdaterParameters.getCsvWriterParameters();
    this.csvWriterFactory = new CsvWriterFactoryImpl(
        csvWriterParameters.getPathsBundle(),
        csvWriterParameters.getMaxNumberOfRowsForFlush());
  }

  /**
   * Add csv record header values to file.
   *
   * @param pivotedCsvRecordHeaderValues csv record header values.
   * @throws CsvUpdaterException exception that encapsulates internal exceptions.
   */
  @Override
  public void addPivotedCsvRecordHeaderValues(
      PivotedCsvRecordHeaderValues pivotedCsvRecordHeaderValues) throws CsvUpdaterException {

    readAndWriteAndMerge((csvWriter,
        csvRecordHeaderValues) -> addCsvRecordHeaderValues(csvWriter, csvRecordHeaderValues,
        pivotedCsvRecordHeaderValues));

  }

  /**
   * First approach: 1. Modify input and write changes in output. 2. Delete input file. 3. Rename
   * output as input.
   * <p>TODO: Do not delete input file: Rename it and send it to SUCCESSFUL or ERROR folder.</p>
   */
  private void readAndWriteAndMerge(CsvRecordHeaderValuesConsumer consumer)
      throws CsvUpdaterException {
    readAndWrite(consumer);
    mergeInputAndOutputPath();
  }

  private void readAndWrite(CsvRecordHeaderValuesConsumer consumer)
      throws CsvUpdaterException {

    try (CsvReader csvReader = new CsvReaderImpl(csvReaderParameters)) {
      readAndWrite(csvReader, consumer);
    } catch (CsvReaderException | IOException e) {
      throw new CsvUpdaterException(e);
    }

  }

  private void readAndWrite(CsvReader csvReader, CsvRecordHeaderValuesConsumer consumer)
      throws CsvUpdaterException {

    try (CsvWriter csvWriter = csvWriterFactory.create(
        csvWriterParameters.getCsvRecordHeaderOrder(),
        csvWriterParameters.getOutputFilename())) {
      readAndWrite(csvReader, csvWriter, consumer);
    } catch (CsvWriterFactoryException | IOException | CsvReaderException e) {
      throw new CsvUpdaterException(e);
    }

  }

  private void readAndWrite(CsvReader csvReader, CsvWriter csvWriter,
      CsvRecordHeaderValuesConsumer consumer)
      throws CsvReaderException {

    csvReader
        .readCsvRecordHeaderValues()
        .map(EitherUtils.liftConsumer(
            csvRecordHeaderValues -> consumer.accept(csvWriter, csvRecordHeaderValues)))
        .filter(Objects::nonNull)
        .forEach(either -> logger
            .error("Exception while applying consumer to file", (Exception) either.getLeft()));

  }


  private void mergeInputAndOutputPath() throws CsvUpdaterException {
    try {
      mergeInputAndOutputPath_WithoutManageExceptions();
    } catch (IOException e) {
      throw new CsvUpdaterException(e);
    }
  }

  private void mergeInputAndOutputPath_WithoutManageExceptions() throws IOException {

    Files.delete(csvReaderParameters.getPath());
    Path outputPath = Paths.get(
        csvWriterParameters.getPathsBundle().getDirectory() + FileSystems.getDefault()
            .getSeparator()
            + csvWriterParameters.getOutputFilename());
    Files.move(outputPath, csvReaderParameters.getPath());

  }

  private void addCsvRecordHeaderValues(CsvWriter csvWriter,
      CsvRecordHeaderValues csvRecordHeaderValues,
      PivotedCsvRecordHeaderValues pivotedCsvRecordHeaderValues) throws CsvUpdaterException {
    try {
      addCsvRecordHeaderValues_WithoutManagementException(csvWriter, csvRecordHeaderValues,
          pivotedCsvRecordHeaderValues);
    } catch (CsvWriterException e) {
      throw new CsvUpdaterException(e);
    }

  }

  private void addCsvRecordHeaderValues_WithoutManagementException(CsvWriter csvWriter,
      CsvRecordHeaderValues csvRecordHeaderValues,
      PivotedCsvRecordHeaderValues pivotedCsvRecordHeaderValues) throws CsvWriterException {

    String pivotHeader = pivotedCsvRecordHeaderValues.getPivotHeader();
    String pivotValue = csvRecordHeaderValues.getValue(pivotHeader);
    CsvRecordHeaderValues newCsvRecordHeaderValues =
        pivotedCsvRecordHeaderValues.getCsvRecordHeaderValues(pivotValue);
    csvRecordHeaderValues.merge(newCsvRecordHeaderValues);

    csvWriter.writeCsvRecord(csvRecordHeaderValues);


  }

  /**
   * Delete columns of csv.
   *
   * @param headers headers to be deleted.
   */
  @Override
  public void deleteColumns(Set<String> headers) throws CsvUpdaterException {
    readAndWriteAndMerge(new DeleteColumnConsumer(headers));
  }

  private class DeleteColumnConsumer extends CsvRecordHeaderValuesCopyConsumer {

    private Set<String> columnsToBeDeleted;
    private CsvWriter csvWriter;

    public DeleteColumnConsumer(Set<String> columnsToBeDeleted) {
      this.columnsToBeDeleted = columnsToBeDeleted;
    }

    @Override
    public void accept(CsvWriter csvWriter, CsvRecordHeaderValues csvRecordHeaderValues)
        throws CsvUpdaterException {

      filterColumnsInCsvWriter(csvWriter);
      filterHeadersInCsvRecordHeaderValues(csvRecordHeaderValues);

      super.accept(csvWriter, csvRecordHeaderValues);

    }

    private void filterColumnsInCsvWriter(CsvWriter csvWriter) {

      if (this.csvWriter == null) {

        CsvRecordHeaderOrder csvRecordHeaderOrder = csvWriter.getCsvRecordHeaderOrder();
        csvRecordHeaderOrder.removeHeaders(columnsToBeDeleted);

        this.csvWriter = csvWriter;

      }
    }

    private void filterHeadersInCsvRecordHeaderValues(CsvRecordHeaderValues csvRecordHeaderValues) {
      Map<String, String> headerValueMap = csvRecordHeaderValues.getHeaderValueMap();
      columnsToBeDeleted.stream().forEach(header -> headerValueMap.remove(header));
    }

  }

  /**
   * Rename columns of csv.
   *
   * @param oldHeaderToNewHeaderMap map old header - new header.
   */
  @Override
  public void renameColumns(Map<String, String> oldHeaderToNewHeaderMap)
      throws CsvUpdaterException {
    readAndWriteAndMerge(new RenameConsumer(oldHeaderToNewHeaderMap));
  }

  private class RenameConsumer extends CsvRecordHeaderValuesCopyConsumer {

    private Map<String, String> oldHeaderToNewHeaderMap;
    private CsvWriter csvWriter;

    public RenameConsumer(Map<String, String> oldHeaderToNewHeaderMap) {
      this.oldHeaderToNewHeaderMap = oldHeaderToNewHeaderMap;
    }

    @Override
    public void accept(CsvWriter csvWriter, CsvRecordHeaderValues csvRecordHeaderValues)
        throws CsvUpdaterException {

      renameCsvWriterColumns(csvWriter);
      renameCsvRecordHeaderValues(csvRecordHeaderValues);

      super.accept(csvWriter, csvRecordHeaderValues);

    }

    private void renameCsvWriterColumns(CsvWriter csvWriter) throws CsvUpdaterException {
      try {
        renameCsvWriterColumns_WithutManagementException(csvWriter);
      } catch (CsvWriterException e) {
        throw new CsvUpdaterException(e);
      }
    }

    private void renameCsvWriterColumns_WithutManagementException(CsvWriter csvWriter)
        throws CsvWriterException {

      if (this.csvWriter == null) {
        this.csvWriter = csvWriter;
        CsvRecordHeaderOrder csvRecordHeaderOrder = csvWriter.getCsvRecordHeaderOrder();
        csvRecordHeaderOrder.renameHeaders(oldHeaderToNewHeaderMap);
        csvWriter.setCsvRecordHeaderOrder(csvRecordHeaderOrder);

      }

    }

    private void renameCsvRecordHeaderValues(CsvRecordHeaderValues csvRecordHeaderValues) {

      Map<String, String> headerValueMap = csvRecordHeaderValues.getHeaderValueMap();
      for (Entry<String, String> entry : oldHeaderToNewHeaderMap.entrySet()) {

        String oldHeader = entry.getKey();
        String newHeader = entry.getValue();

        String value = headerValueMap.get(oldHeader);
        if (value != null) {
          headerValueMap.remove(oldHeader);
          headerValueMap.put(newHeader, value);
        }

      }

    }

  }

  /**
   * Apply consumer to each csv record header values.
   *
   * @param consumer csv record header values consumer.
   * @throws CsvUpdaterException excetion that encapsulates internal exceptions.
   */
  @Override
  public void applyConsumer(CsvRecordHeaderValuesConsumer consumer) throws CsvUpdaterException {
    readAndWriteAndMerge(consumer);
  }

}
