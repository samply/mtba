package de.samply.file.csv.writer;

public class CsvWriterException extends Exception {

  public CsvWriterException(String message) {
    super(message);
  }

  public CsvWriterException(String message, Throwable cause) {
    super(message, cause);
  }

  public CsvWriterException(Throwable cause) {
    super(cause);
  }

  public CsvWriterException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
