package de.samply.utils;

import de.samply.file.csv.reader.CsvReaderImpl;
import de.samply.file.csv.reader.CsvReaderParameters;
import de.samply.file.csv.updater.CsvUpdaterFactoryImpl;
import de.samply.file.csv.updater.CsvUpdaterParameters;
import de.samply.file.csv.writer.CsvWriterParameters;

public class FileConfigUtils {

  public static void addFileConfig(FileConfig fileConfig, CsvReaderParameters csvReaderParameters) {
    csvReaderParameters.setCharset(fileConfig.getFileCharset());
    csvReaderParameters.setDelimiter(fileConfig.getCsvDelimiter());
    csvReaderParameters.setEndOfLine(fileConfig.getEndOfLine());
  }

  public static void addFileConfig(FileConfig fileConfig, CsvWriterParameters csvWriterParameters) {
    csvWriterParameters.setCharset(fileConfig.getFileCharset());
    csvWriterParameters.setDelimiter(fileConfig.getCsvDelimiter());
    csvWriterParameters.setEndOfLine(fileConfig.getEndOfLine());
  }

  public static void addFileConfig(FileConfig fileConfig, CsvUpdaterParameters csvUpdaterParameters) {
    csvUpdaterParameters.setCharset(fileConfig.getFileCharset());
    csvUpdaterParameters.setDelimiter(fileConfig.getCsvDelimiter());
    csvUpdaterParameters.setEndOfLine(fileConfig.getEndOfLine());
  }

  public static void addFileConfig(FileConfig fileConfig, CsvUpdaterFactoryImpl csvUpdaterFactory) {
    csvUpdaterFactory.setCharset(fileConfig.getFileCharset());
    csvUpdaterFactory.setDelimiter(fileConfig.getCsvDelimiter());
    csvUpdaterFactory.setEndOfLine(fileConfig.getEndOfLine());
  }

}
