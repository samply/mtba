package de.samply.file.csv.writer;

import de.samply.file.bundle.PathsBundle;
import de.samply.file.csv.CsvRecordHeaderOrder;

public class CsvWriterFactoryImpl implements CsvWriterFactory {

  private PathsBundle pathsBundle;
  private Integer maxNumberOfRowsPerFlush = 100;

  /**
   * Generates csv writers.
   *
   * @param pathsBundle paths bundle
   */
  public CsvWriterFactoryImpl(PathsBundle pathsBundle) {
    this.pathsBundle = pathsBundle;
  }

  /**
   * Generates csv writers.
   *
   * @param pathsBundle             paths bundle.
   * @param maxNumberOfRowsPerFlush maximal number of rows to be written per flush.
   */
  public CsvWriterFactoryImpl(PathsBundle pathsBundle, Integer maxNumberOfRowsPerFlush) {
    this(pathsBundle);
    this.maxNumberOfRowsPerFlush = maxNumberOfRowsPerFlush;
  }


  /**
   * Generate csv writer.
   *
   * @param csvRecordHeaderOrder order of csv record headers.
   * @param outputFilename       output filename.
   * @return csv writer.
   * @throws CsvWriterFactoryException exception that catches internal exceptions.
   */
  @Override
  public CsvWriter create(CsvRecordHeaderOrder csvRecordHeaderOrder, String outputFilename)
      throws CsvWriterFactoryException {
    try {
      return create_WithoutManagementException(csvRecordHeaderOrder, outputFilename);
    } catch (CsvWriterException e) {
      throw new CsvWriterFactoryException(e);
    }
  }


  private CsvWriter create_WithoutManagementException(CsvRecordHeaderOrder csvRecordHeaderOrder,
      String outputFilename)
      throws CsvWriterException {

    CsvWriterParameters csvWriterParameters = generateCsvWriterParameters(csvRecordHeaderOrder,
        outputFilename);
    return new CsvWriterImpl(csvWriterParameters);

  }

  private CsvWriterParameters generateCsvWriterParameters(CsvRecordHeaderOrder csvRecordHeaderOrder,
      String outputFilename) {

    CsvWriterParameters csvWriterParameters = new CsvWriterParameters();

    csvWriterParameters.setCsvRecordHeaderOrder(csvRecordHeaderOrder);
    csvWriterParameters.setOutputFilename(outputFilename);
    csvWriterParameters.setPathsBundle(pathsBundle);

    return csvWriterParameters;

  }

}
