package de.samply.file.csv.reader;

import de.samply.file.csv.CsvRecordHeaderValues;
import java.io.Closeable;
import java.util.stream.Stream;

public interface CsvReader extends Closeable {

  Stream<CsvRecordHeaderValues> fetchCsvRecordHeaderValues() throws CsvReaderException;

}
