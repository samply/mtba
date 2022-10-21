package de.samply.file.csv;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.csv.CSVRecord;

public class CsvRecordHeaderValues {

  private Map<String, String> headerValueMap = new HashMap<>();

  public CsvRecordHeaderValues(Set<String> headers) {
    this(headers, null);
  }
  /**
   * Values of a csv record for specified headers.
   *
   * @param headers   headers to be considered in csv record.
   * @param csvRecord csv record where the information is extracted.
   */
  public CsvRecordHeaderValues(Set<String> headers, CSVRecord csvRecord) {

    if (csvRecord != null){
      if (headers.size() > 0) {
        for (String header : headers) {
          String value = csvRecord.get(header);
          if (value != null) {
            headerValueMap.put(header, value);
          }
        }
      } else {
        headerValueMap = csvRecord.toMap();
      }
    }

  }

  public Map<String, String> getHeaderValueMap() {
    return headerValueMap;
  }

  public String getValue(String header) {
    return getHeaderValueMap().get(header);
  }

  /**
   * Merges current csv record header value with input.
   *
   * @param csvRecordHeaderValues csv record header values.
   */
  public void merge(CsvRecordHeaderValues csvRecordHeaderValues) {
    for (Entry<String, String> entry : csvRecordHeaderValues.getHeaderValueMap().entrySet()) {
      headerValueMap.put(entry.getKey(), entry.getValue());
    }
  }

}
