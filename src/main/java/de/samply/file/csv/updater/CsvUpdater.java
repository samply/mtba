package de.samply.file.csv.updater;

import de.samply.file.csv.reader.CsvReaderParameters;
import de.samply.file.csv.writer.CsvWriter;
import de.samply.file.csv.writer.CsvWriterException;

public class CsvUpdater extends CsvWriter {

  private CsvReaderParameters csvReaderParameters;

  /**
   * Updates a csv file with new data.
   *
   * @param csvUpdaterParameters csv updater parameters.
   * @throws CsvWriterException exception that encapsulates internal exceptions.
   */
  public CsvUpdater(CsvUpdaterParameters csvUpdaterParameters) throws CsvWriterException {
    super(csvUpdaterParameters.getCsvWriterParameters());

    this.csvReaderParameters = csvUpdaterParameters.getCsvReaderParameters();

  }


}
