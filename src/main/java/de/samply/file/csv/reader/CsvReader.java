package de.samply.file.csv.reader;

import de.samply.file.csv.CsvRecordHeaderValues;
import de.samply.file.csv.CsvRecordHeaderValuesIterator;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class CsvReader {

  /**
   * Fetch values of csv records for specified headers.
   *
   * @param csvReaderParameters csv reader parameters.
   * @return iterator of header-values of csv records.
   * @throws CsvReaderException exception that encapsulates all exceptions within the class.
   */
  public static Iterator<CsvRecordHeaderValues> fetchCsvRecordHeaderValues(
      CsvReaderParameters csvReaderParameters) throws CsvReaderException {

    return (csvReaderParameters != null && csvReaderParameters.getPath() != null)
        ? fetchCsvRecordHeaderValues_WithoutCheck(csvReaderParameters)
        : Collections.emptyIterator();

  }

  private static Iterator<CsvRecordHeaderValues> fetchCsvRecordHeaderValues_WithoutCheck(
      CsvReaderParameters csvReaderParameters) throws CsvReaderException {

    Iterator<CSVRecord> iterator = fetchCsvRecords(csvReaderParameters).iterator();
    Set<String> headers = csvReaderParameters.getHeaders();

    return new CsvRecordHeaderValuesIterator(iterator, headers);

  }

  private static Iterable<CSVRecord> fetchCsvRecords(CsvReaderParameters csvReaderParameters)
      throws CsvReaderException {

    try (Reader reader = createReader(csvReaderParameters)) {
      return fetchCsvRecords(csvReaderParameters, reader);
    } catch (IOException e) {
      throw new CsvReaderException(e);
    }

  }

  private static Iterable<CSVRecord> fetchCsvRecords(CsvReaderParameters csvReaderParameters,
      Reader reader)
      throws IOException {

    CSVFormat csvFormat = CSVFormat.DEFAULT;
    if (!csvReaderParameters.readAllHeaders()) {
      String[] headers = (String[]) csvReaderParameters.getHeaders().toArray();
      csvFormat = csvFormat.withHeader(headers);
    }

    return csvFormat
        .withFirstRecordAsHeader()
        .withIgnoreEmptyLines()
        .withIgnoreHeaderCase()
        .parse(reader);

  }

  private static Reader createReader(CsvReaderParameters csvReaderParameters) throws IOException {

    Path path = csvReaderParameters.getPath();
    return Files.newBufferedReader(path);

  }


}
