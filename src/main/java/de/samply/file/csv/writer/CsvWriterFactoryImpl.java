package de.samply.file.csv.writer;

import de.samply.file.bundle.PathsBundle;
import de.samply.file.csv.CsvRecordHeaderOrder;
import de.samply.spring.MtbaConst;
import java.nio.charset.Charset;

public class CsvWriterFactoryImpl implements CsvWriterFactory {

  private PathsBundle pathsBundle;
  private Integer maxNumberOfRowsPerFlush = 100;
  private String csvDelimiter = MtbaConst.DEFAULT_CSV_DELIMITER;
  private String fileEndOfLine = MtbaConst.DEFAULT_END_OF_LINE;
  private Charset charset = MtbaConst.DEFAULT_CHARSET;

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
    csvWriterParameters.setEndOfLine(fileEndOfLine);
    csvWriterParameters.setCharset(charset);
    csvWriterParameters.setDelimiter(csvDelimiter);
    csvWriterParameters.setMaxNumberOfRowsForFlush(maxNumberOfRowsPerFlush);

    return csvWriterParameters;

  }

  public void setCsvDelimiter(String csvDelimiter) {
    this.csvDelimiter = csvDelimiter;
  }

  public void setFileEndOfLine(String fileEndOfLine) {
    this.fileEndOfLine = fileEndOfLine;
  }

  public void setCharset(Charset charset) {
    this.charset = charset;
  }

}
