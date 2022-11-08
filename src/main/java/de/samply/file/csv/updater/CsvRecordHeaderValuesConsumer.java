package de.samply.file.csv.updater;

import de.samply.file.csv.CsvRecordHeaderOrder;
import de.samply.file.csv.CsvRecordHeaderValues;
import de.samply.file.csv.writer.CsvWriter;

public interface CsvRecordHeaderValuesConsumer {

  void accept(CsvRecordHeaderValues csvRecordHeaderValues) throws CsvUpdaterException;

  CsvRecordHeaderOrder prepareHeaders(CsvRecordHeaderOrder csvRecordHeaderOrder)
      throws CsvUpdaterException;

}
