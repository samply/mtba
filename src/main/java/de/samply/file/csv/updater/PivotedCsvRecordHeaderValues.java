package de.samply.file.csv.updater;

import de.samply.file.csv.CsvRecordHeaderValues;
import java.util.HashMap;
import java.util.Map;

public class PivotedCsvRecordHeaderValues {

  private String pivotHeader;
  private Map<String, CsvRecordHeaderValues> pivotValueTOheaderValuesMap = new HashMap<>();

  public PivotedCsvRecordHeaderValues(String pivotHeader) {
    this.pivotHeader = pivotHeader;
  }

  /**
   * Add csv record header values.
   *
   * @param csvRecordHeaderValues csv record header values.
   */
  public void addCsvRecordHeaderValues(CsvRecordHeaderValues csvRecordHeaderValues) {

    if (pivotHeader != null && csvRecordHeaderValues != null
        && csvRecordHeaderValues.getValue(pivotHeader) != null) {
      String pivotValue = csvRecordHeaderValues.getValue(pivotHeader);
      pivotValueTOheaderValuesMap.put(pivotValue, csvRecordHeaderValues);
    }

  }

  public CsvRecordHeaderValues getCsvRecordHeaderValues(String pivotValue) {
    return pivotValueTOheaderValuesMap.get(pivotValue);
  }

  public String getPivotHeader() {
    return pivotHeader;
  }

}
