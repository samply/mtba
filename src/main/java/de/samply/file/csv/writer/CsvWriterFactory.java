package de.samply.file.csv.writer;

import de.samply.file.csv.CsvRecordHeaderOrder;

public interface CsvWriterFactory {

  CsvWriter create(CsvRecordHeaderOrder csvRecordHeaderOrder, String outputFilename)
      throws CsvWriterFactoryException;

}
