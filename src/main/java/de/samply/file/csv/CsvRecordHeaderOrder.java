package de.samply.file.csv;

import java.util.HashMap;
import java.util.Map;

public class CsvRecordHeaderOrder {

  private Map<String, Integer> headerOrderMap = new HashMap<>();

  public void addHeader (String header, int order){
    headerOrderMap.put(header, order);
  }

  public Integer getOrder (String header){
    return headerOrderMap.get(header);
  }

}
