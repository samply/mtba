package de.samply.file.csv.writer;

public class CsvWriterFactoryException extends Exception {

  public CsvWriterFactoryException(String message) {
    super(message);
  }

  public CsvWriterFactoryException(String message, Throwable cause) {
    super(message, cause);
  }

  public CsvWriterFactoryException(Throwable cause) {
    super(cause);
  }

  public CsvWriterFactoryException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
