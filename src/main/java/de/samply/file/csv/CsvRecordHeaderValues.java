package de.samply.file.csv;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.csv.CSVRecord;

public class CsvRecordHeaderValues {

  private Map<String, String> headerValueMap = new HashMap<>();

  /**
   * Values of a csv record for specified headers.
   *
   * @param headers   headers to be considered in csv record.
   * @param csvRecord csv record where the information is extracted.
   */
  public CsvRecordHeaderValues(Set<String> headers, CSVRecord csvRecord) {

    for (String header : headers) {
      String value = csvRecord.get(header);
      if (value != null) {
        headerValueMap.put(header, value);
      }
    }

  }

  public Map<String, String> getHeaderValueMap() {
    return headerValueMap;
  }

  public String getValue(String header) {
    return getHeaderValueMap().get(header);
  }

}
