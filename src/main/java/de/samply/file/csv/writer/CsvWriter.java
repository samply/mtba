package de.samply.file.csv.writer;

import de.samply.file.csv.CsvRecordHeaderOrder;
import de.samply.file.csv.CsvRecordHeaderValues;
import java.io.Closeable;

public interface CsvWriter extends Closeable {

  void writeCsvRecord(CsvRecordHeaderValues csvRecordHeaderValues) throws CsvWriterException;

  CsvRecordHeaderOrder getCsvRecordHeaderOrder();
  void setCsvRecordHeaderOrder (CsvRecordHeaderOrder csvRecordHeaderOrder) throws CsvWriterException;

}
