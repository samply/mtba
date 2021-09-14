package de.samply.file.csv.updater;

import de.samply.file.csv.CsvRecordHeaderValues;
import de.samply.file.csv.reader.CsvReader;
import de.samply.file.csv.reader.CsvReaderException;
import de.samply.file.csv.reader.CsvReaderParameters;
import de.samply.file.csv.writer.CsvWriter;
import de.samply.file.csv.writer.CsvWriterException;
import de.samply.file.csv.writer.CsvWriterFactory;
import de.samply.file.csv.writer.CsvWriterFactoryException;
import de.samply.file.csv.writer.CsvWriterParameters;
import de.samply.utils.EitherUtils;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvUpdater {

  private final Logger logger = LoggerFactory.getLogger(CsvUpdater.class);

  private CsvReaderParameters csvReaderParameters;
  private CsvWriterParameters csvWriterParameters;
  private CsvWriterFactory csvWriterFactory;

  /**
   * Updates a csv file with new data.
   *
   * @param csvUpdaterParameters csv updater parameters.
   * @throws CsvWriterException exception that encapsulates internal exceptions.
   */
  public CsvUpdater(CsvUpdaterParameters csvUpdaterParameters) throws CsvWriterException {
    this.csvReaderParameters = csvUpdaterParameters.getCsvReaderParameters();
    this.csvWriterParameters = csvUpdaterParameters.getCsvWriterParameters();
    this.csvWriterFactory = new CsvWriterFactory(
        csvWriterParameters.getOutputFolderPath(),
        csvWriterParameters.getMaxNumberOfRowsForFlush());
  }

  /**
   * Add csv record header values to file.
   *
   * @param csvRecordHeaderValues csv record header values.
   * @throws CsvUpdaterException exception that encapsulates internal exceptions.
   */
  public void addCsvRecordHeaderValues(
      PivotedCsvRecordHeaderValues csvRecordHeaderValues) throws CsvUpdaterException {

    addCsvRecordHeaderValues_WithoutInputAndOutputMerge(csvRecordHeaderValues);
    mergeInputAndOutputPath();

  }

  /**
   * First approach: 1. Modify input and write changes in output. 2. Delete input file. 3. Rename
   * output as input.
   * <p>
   * TODO: Do not delete input file: Rename it and send it to SUCCESSFUL or ERROR folder.
   */
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
        csvWriterParameters.getOutputFolderPath() + FileSystems.getDefault().getSeparator()
            + csvWriterParameters.getOutputFilename());
    Files.move(outputPath, csvReaderParameters.getPath());

  }

  private void addCsvRecordHeaderValues_WithoutInputAndOutputMerge(
      PivotedCsvRecordHeaderValues csvRecordHeaderValues) throws CsvUpdaterException {

    try (CsvWriter csvWriter = csvWriterFactory.create(
        csvWriterParameters.getCsvRecordHeaderOrder(),
        csvWriterParameters.getOutputFilename())) {
      addCsvRecordHeaderValues(csvWriter, csvRecordHeaderValues);
    } catch (CsvWriterFactoryException | IOException e) {
      throw new CsvUpdaterException(e);
    }
  }

  private void addCsvRecordHeaderValues(CsvWriter csvWriter,
      PivotedCsvRecordHeaderValues csvRecordHeaderValues) throws CsvUpdaterException {

    try (CsvReader csvReader = new CsvReader(csvReaderParameters)) {
      addCsvRecordHeaderValues(csvWriter, csvReader, csvRecordHeaderValues);
    } catch (CsvReaderException | IOException e) {
      throw new CsvUpdaterException(e);
    }

  }

  private void addCsvRecordHeaderValues(CsvWriter csvWriter, CsvReader csvReader,
      PivotedCsvRecordHeaderValues pivotedCsvRecordHeaderValues) throws CsvReaderException {

    csvReader
        .fetchCsvRecordHeaderValues()
        .map(EitherUtils.liftConsumer(
            csvRecordHeaderValues -> addCsvRecordHeaderValues(csvWriter, csvRecordHeaderValues,
                pivotedCsvRecordHeaderValues)))
        .filter(Objects::nonNull)
        .forEach(either -> logger
            .error("Exception while applying consumer to file", (Exception) either.getLeft()));

  }

  private void addCsvRecordHeaderValues(CsvWriter csvWriter,
      CsvRecordHeaderValues csvRecordHeaderValues,
      PivotedCsvRecordHeaderValues pivotedCsvRecordHeaderValues) throws CsvWriterException {

    String pivotHeader = pivotedCsvRecordHeaderValues.getPivotHeader();
    String pivotValue = csvRecordHeaderValues.getValue(pivotHeader);
    CsvRecordHeaderValues newCsvRecordHeaderValues =
        pivotedCsvRecordHeaderValues.getCsvRecordHeaderValues(pivotValue);
    csvRecordHeaderValues.merge(newCsvRecordHeaderValues);

    csvWriter.addCsvRecord(csvRecordHeaderValues);


  }


}
