package de.samply.file.csv.updater;

import de.samply.file.csv.reader.CsvReaderParameters;

public interface CsvUpdaterFactory {

  CsvUpdater createCsvUpdater(CsvReaderParameters csvReaderParameters)
      throws CsvUpdaterFactoryException;

}
