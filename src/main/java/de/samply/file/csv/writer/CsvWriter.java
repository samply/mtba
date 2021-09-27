package de.samply.file.csv.writer;

import de.samply.file.csv.CsvRecordHeaderOrder;
import de.samply.file.csv.CsvRecordHeaderValues;
import java.io.Closeable;

public interface CsvWriter extends Closeable {

  void addCsvRecord(CsvRecordHeaderValues csvRecordHeaderValues) throws CsvWriterException;

  CsvRecordHeaderOrder getCsvRecordHeaderOrder();

}
