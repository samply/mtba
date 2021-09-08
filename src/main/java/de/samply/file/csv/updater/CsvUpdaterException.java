package de.samply.file.csv.updater;

public class CsvUpdaterException extends Exception {

  public CsvUpdaterException(String message) {
    super(message);
  }

  public CsvUpdaterException(String message, Throwable cause) {
    super(message, cause);
  }

  public CsvUpdaterException(Throwable cause) {
    super(cause);
  }

  public CsvUpdaterException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
