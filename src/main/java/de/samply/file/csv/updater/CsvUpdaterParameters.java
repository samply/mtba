package de.samply.file.csv.updater;

import de.samply.file.csv.reader.CsvReaderParameters;
import de.samply.file.csv.writer.CsvWriterParameters;

public class CsvUpdaterParameters {

  private CsvReaderParameters csvReaderParameters;
  private CsvWriterParameters csvWriterParameters;

  public CsvReaderParameters getCsvReaderParameters() {
    return csvReaderParameters;
  }

  public void setCsvReaderParameters(CsvReaderParameters csvReaderParameters) {
    this.csvReaderParameters = csvReaderParameters;
  }

  public CsvWriterParameters getCsvWriterParameters() {
    return csvWriterParameters;
  }

  public void setCsvWriterParameters(CsvWriterParameters csvWriterParameters) {
    this.csvWriterParameters = csvWriterParameters;
  }
}
