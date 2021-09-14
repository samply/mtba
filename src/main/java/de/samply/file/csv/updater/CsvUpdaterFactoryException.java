package de.samply.file.csv.updater;

public class CsvUpdaterFactoryException extends Exception {

  public CsvUpdaterFactoryException(String message) {
    super(message);
  }

  public CsvUpdaterFactoryException(String message, Throwable cause) {
    super(message, cause);
  }

  public CsvUpdaterFactoryException(Throwable cause) {
    super(cause);
  }

  public CsvUpdaterFactoryException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
