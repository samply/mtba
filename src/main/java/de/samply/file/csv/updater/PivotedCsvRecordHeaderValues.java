package de.samply.file.csv.updater;

import de.samply.file.csv.CsvRecordHeaderValues;
import java.util.HashMap;
import java.util.Map;

public class PivotedCsvRecordHeaderValues {

  private String pivotHeader;
  private Map<String, CsvRecordHeaderValues> pivotValue_headervaluesMap = new HashMap<>();

  public PivotedCsvRecordHeaderValues(String pivotHeader) {
    this.pivotHeader = pivotHeader;
  }

  public void addCsvRecordHeaderValues(CsvRecordHeaderValues csvRecordHeaderValues) {

    if (pivotHeader != null && csvRecordHeaderValues != null
        && csvRecordHeaderValues.getValue(pivotHeader) != null) {
      String pivotValue = csvRecordHeaderValues.getValue(pivotHeader);
      pivotValue_headervaluesMap.put(pivotValue, csvRecordHeaderValues);
    }

  }

  public CsvRecordHeaderValues getCsvRecordHeaderValues (String pivotValue){
    return pivotValue_headervaluesMap.get(pivotValue);
  }

}
