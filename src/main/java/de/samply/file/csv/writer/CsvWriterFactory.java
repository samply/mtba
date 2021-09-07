package de.samply.file.csv.writer;

import de.samply.file.csv.CsvRecordHeaderOrder;

public class CsvWriterFactory {

  private String outputFolderPath;

  /**
   * Generates csv writers.
   *
   * @param outputFolderPath output folder path.
   */
  public CsvWriterFactory(String outputFolderPath) {
    this.outputFolderPath = outputFolderPath;
  }

  /**
   * Generate csv writer.
   *
   * @param csvRecordHeaderOrder order of csv record headers.
   * @param outputFilename       output filename.
   * @return csv writer.
   * @throws CsvWriterFactoryException exception that catches internal exceptions.
   */
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
    return new CsvWriter(csvWriterParameters);

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
