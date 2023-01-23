package de.samply.file.csv.updater;

import de.samply.file.csv.reader.CsvReaderParameters;
import de.samply.file.csv.writer.CsvWriterParameters;
import java.nio.charset.Charset;

public class CsvUpdaterParameters {

  private CsvReaderParameters csvReaderParameters;
  private CsvWriterParameters csvWriterParameters;

  private String delimiter;
  private String endOfLine;
  private Charset charset;

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

  public String getDelimiter() {
    return delimiter;
  }

  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  public String getEndOfLine() {
    return endOfLine;
  }

  public void setEndOfLine(String endOfLine) {
    this.endOfLine = endOfLine;
  }

  public Charset getCharset() {
    return charset;
  }

  public void setCharset(Charset charset) {
    this.charset = charset;
  }

}
