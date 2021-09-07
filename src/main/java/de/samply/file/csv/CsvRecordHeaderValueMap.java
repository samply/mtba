package de.samply.file.csv;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.csv.CSVRecord;

public class CsvRecordHeaderValueMap {

  private Map<String, String> headerValueMap = new HashMap<>();

  /**
   * Values of a csv record under specified headers.
   *
   * @param headers   headers to be considered in csv record.
   * @param csvRecord csv record where the information is extracted.
   */
  public CsvRecordHeaderValueMap(Set<String> headers, CSVRecord csvRecord) {

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

  public Collection<String> getValue(String header) {
    return getHeaderValueMap().values();
  }

}
