package de.samply.file.csv.updater;

import de.samply.file.csv.CsvRecordHeaderValues;
import de.samply.file.csv.writer.CsvWriter;

public interface CsvRecordHeaderValuesConsumer {

  void accept(CsvWriter csvWriter, CsvRecordHeaderValues csvRecordHeaderValues)
      throws CsvUpdaterException;


}
