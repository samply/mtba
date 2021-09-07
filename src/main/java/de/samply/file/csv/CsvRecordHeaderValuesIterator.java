package de.samply.file.csv;

import java.util.Iterator;
import java.util.Set;
import org.apache.commons.csv.CSVRecord;

public class CsvRecordHeaderValuesIterator implements Iterator<CsvRecordHeaderValues> {

  private Iterator<CSVRecord> csvRecordIterator;
  private Set<String> csvRecordHeaders;


  public CsvRecordHeaderValuesIterator(
      Iterator<CSVRecord> csvRecordIterator, Set<String> csvRecordHeaders) {
    this.csvRecordIterator = csvRecordIterator;
    this.csvRecordHeaders = csvRecordHeaders;
  }

  @Override
  public boolean hasNext() {
    return csvRecordIterator.hasNext();
  }

  @Override
  public CsvRecordHeaderValues next() {
    return new CsvRecordHeaderValues(csvRecordHeaders, csvRecordIterator.next());
  }

}
