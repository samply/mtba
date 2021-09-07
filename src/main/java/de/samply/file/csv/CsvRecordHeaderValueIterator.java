package de.samply.file.csv;

import java.util.Iterator;
import java.util.Set;
import org.apache.commons.csv.CSVRecord;

public class CsvRecordHeaderValueIterator implements Iterator<CsvRecordHeaderValueMap> {

  private Iterator<CSVRecord> csvRecordIterator;
  private Set<String> csvRecordHeaders;


  public CsvRecordHeaderValueIterator(
      Iterator<CSVRecord> csvRecordIterator, Set<String> csvRecordHeaders) {
    this.csvRecordIterator = csvRecordIterator;
    this.csvRecordHeaders = csvRecordHeaders;
  }

  @Override
  public boolean hasNext() {
    return csvRecordIterator.hasNext();
  }

  @Override
  public CsvRecordHeaderValueMap next() {
    return new CsvRecordHeaderValueMap(csvRecordHeaders, csvRecordIterator.next());
  }

}
