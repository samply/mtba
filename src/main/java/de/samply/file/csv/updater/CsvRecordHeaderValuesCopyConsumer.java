package de.samply.file.csv.updater;

import de.samply.file.csv.CsvRecordHeaderOrder;
import de.samply.file.csv.CsvRecordHeaderValues;
import de.samply.file.csv.writer.CsvWriter;
import de.samply.file.csv.writer.CsvWriterException;

public class CsvRecordHeaderValuesCopyConsumer implements CsvRecordHeaderValuesConsumer {

  @Override
  public void accept(CsvWriter csvWriter, CsvRecordHeaderValues csvRecordHeaderValues)
      throws CsvUpdaterException {
    try {
      csvWriter.writeCsvRecord(csvRecordHeaderValues);
    } catch (CsvWriterException e) {
      throw new CsvUpdaterException(e);
    }
  }

  @Override
  public CsvRecordHeaderOrder prepareHeaders(CsvRecordHeaderOrder csvRecordHeaderOrder)
      throws CsvUpdaterException {
    return csvRecordHeaderOrder;
  }


}
