package de.samply.file.csv.writer;

import de.samply.file.csv.CsvRecordHeaderOrder;

public class CsvWriterParameters {

  private CsvRecordHeaderOrder csvRecordHeaderOrder;
  private String outputFolderPath;
  private String outputFilename;
  private Integer maxNumberOfRowsForFlush;

  public Integer getMaxNumberOfRowsForFlush() {
    return maxNumberOfRowsForFlush;
  }

  public void setMaxNumberOfRowsForFlush(Integer maxNumberOfRowsForFlush) {
    this.maxNumberOfRowsForFlush = maxNumberOfRowsForFlush;
  }

  public CsvRecordHeaderOrder getCsvRecordHeaderOrder() {
    return csvRecordHeaderOrder;
  }

  public void setCsvRecordHeaderOrder(CsvRecordHeaderOrder csvRecordHeaderOrder) {
    this.csvRecordHeaderOrder = csvRecordHeaderOrder;
  }

  public String getOutputFolderPath() {
    return outputFolderPath;
  }

  public void setOutputFolderPath(String outputFolderPath) {
    this.outputFolderPath = outputFolderPath;
  }

  public String getOutputFilename() {
    return outputFilename;
  }

  public void setOutputFilename(String outputFilename) {
    this.outputFilename = outputFilename;
  }

}
