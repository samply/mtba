package de.samply.file.csv.updater;

import java.nio.file.Path;

public interface CsvUpdaterFactory {

  CsvUpdater createCsvUpdater(Path inputPath) throws CsvUpdaterFactoryException;

}
