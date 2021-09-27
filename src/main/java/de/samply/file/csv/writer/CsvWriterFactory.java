package de.samply.file.csv.writer;

import de.samply.file.csv.CsvRecordHeaderOrder;

public class CsvWriterFactory {

  private String outputFolderPath;
  private Integer maxNumberOfRowsPerFlush = 100;

  /**
   * Generates csv writers.
   *
   * @param outputFolderPath output folder path.
   */
  public CsvWriterFactory(String outputFolderPath) {
    this.outputFolderPath = outputFolderPath;
  }

  /**
   * Generates csv writers.
   *
   * @param outputFolderPath        output folder path.
   * @param maxNumberOfRowsPerFlush maximal number of rows to be written per flush.
   */
  public CsvWriterFactory(String outputFolderPath, Integer maxNumberOfRowsPerFlush) {
    this.outputFolderPath = outputFolderPath;
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
  public CsvWriterImpl create(CsvRecordHeaderOrder csvRecordHeaderOrder, String outputFilename)
      throws CsvWriterFactoryException {
    try {
      return create_WithoutManagementException(csvRecordHeaderOrder, outputFilename);
    } catch (CsvWriterException e) {
      throw new CsvWriterFactoryException(e);
    }
  }


  private CsvWriterImpl create_WithoutManagementException(CsvRecordHeaderOrder csvRecordHeaderOrder,
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
    csvWriterParameters.setOutputFolderPath(outputFolderPath);

    return csvWriterParameters;

  }

}
