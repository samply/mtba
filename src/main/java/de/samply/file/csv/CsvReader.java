package de.samply.file.csv;

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
  public Iterator<CsvRecordHeaderValueMap> fetchCsvRecordHeaderValues(
      CsvReaderParameters csvReaderParameters) throws CsvReaderException {

    return (csvReaderParameters != null && csvReaderParameters.getFilename() != null
        && csvReaderParameters.getPathsBundle() != null)
        ? fetchCsvRecordHeaderValues_WithoutCheck(csvReaderParameters)
        : Collections.emptyIterator();

  }

  private Iterator<CsvRecordHeaderValueMap> fetchCsvRecordHeaderValues_WithoutCheck(
      CsvReaderParameters csvReaderParameters) throws CsvReaderException {

    Iterator<CSVRecord> iterator = fetchCsvRecords(csvReaderParameters).iterator();
    Set<String> headers = csvReaderParameters.getHeaders();

    return new CsvRecordHeaderValueIterator(iterator, headers);

  }

  private Iterable<CSVRecord> fetchCsvRecords(CsvReaderParameters csvReaderParameters)
      throws CsvReaderException {

    try (Reader reader = createReader(csvReaderParameters)) {
      return fetchCsvRecords(csvReaderParameters, reader);
    } catch (IOException e) {
      throw new CsvReaderException(e);
    }

  }

  private Iterable<CSVRecord> fetchCsvRecords(CsvReaderParameters csvReaderParameters,
      Reader reader)
      throws IOException {

    String[] headers = (String[]) csvReaderParameters.getHeaders().toArray();
    return CSVFormat.DEFAULT
        .withHeader(headers)
        .withFirstRecordAsHeader()
        .parse(reader);

  }

  private Reader createReader(CsvReaderParameters csvReaderParameters) throws IOException {

    String filename = csvReaderParameters.getFilename();
    Path path = csvReaderParameters.getPathsBundle().getPath(filename);
    return Files.newBufferedReader(path);

  }


}
