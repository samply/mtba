package de.samply.file.csv.updater;

import java.util.Map;
import java.util.Set;

public interface CsvUpdater {

  void addPivotedCsvRecordHeaderValues(PivotedCsvRecordHeaderValues pivotedCsvRecordHeaderValues)
      throws CsvUpdaterException;

  void deleteColumns(Set<String> headers) throws CsvUpdaterException;

  void renameColumns(Map<String, String> oldHeaderToNewHeaderMap) throws CsvUpdaterException;

}
