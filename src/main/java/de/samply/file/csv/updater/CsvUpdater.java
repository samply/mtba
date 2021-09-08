package de.samply.file.csv.updater;

import de.samply.file.csv.writer.CsvWriter;
import de.samply.file.csv.writer.CsvWriterException;
import de.samply.file.csv.writer.CsvWriterParameters;

public class CsvUpdater extends CsvWriter {

  /**
   * Writes csv records in csv file.
   *
   * @param csvWriterParameters csv writer parameters.
   * @throws CsvWriterException exception that encapsulates internal exceptions.
   */
  public CsvUpdater(CsvWriterParameters csvWriterParameters)
      throws CsvWriterException {
    super(csvWriterParameters);
  }
}
