package de.samply.file.csv.writer;

import de.samply.file.bundle.PathsBundle;
import de.samply.file.csv.CsvRecordHeaderOrder;

public class CsvWriterParameters {

  private CsvRecordHeaderOrder csvRecordHeaderOrder;
  private PathsBundle pathsBundle;
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

  public PathsBundle getPathsBundle() {
    return pathsBundle;
  }

  public void setPathsBundle(PathsBundle pathsBundle) {
    this.pathsBundle = pathsBundle;
  }

  public String getOutputFilename() {
    return outputFilename;
  }

  public void setOutputFilename(String outputFilename) {
    this.outputFilename = outputFilename;
  }

}
