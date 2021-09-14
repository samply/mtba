package de.samply.file.csv.reader;

import de.samply.file.csv.CsvRecordHeaderValues;
import de.samply.file.csv.CsvRecordHeaderValuesIterator;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class CsvReader implements Closeable {

  private Reader reader;
  private CsvReaderParameters csvReaderParameters;

  public CsvReader(CsvReaderParameters csvReaderParameters) throws CsvReaderException {
    this.csvReaderParameters = csvReaderParameters;
    this.reader = createReader(csvReaderParameters);
  }

  /**
   * Fetch values of csv records for specified headers.
   *
   * @return stream of header-values of csv records.
   * @throws CsvReaderException exception that encapsulates all exceptions within the class.
   */
  public Stream<CsvRecordHeaderValues> fetchCsvRecordHeaderValues() throws CsvReaderException {

    return (csvReaderParameters != null && csvReaderParameters.getPath() != null)
        ? fetchCsvRecordHeaderValues_WithoutCheck()
        : Stream.empty();

  }

  private Stream<CsvRecordHeaderValues> convertIteratorToStream(
      Iterator<CsvRecordHeaderValues> csvRecordHeaderValuesIterator) {

    Spliterator<CsvRecordHeaderValues> csvRecordHeaderValuesSpliterator =
        Spliterators.spliteratorUnknownSize(csvRecordHeaderValuesIterator, Spliterator.ORDERED);
    return StreamSupport.stream(csvRecordHeaderValuesSpliterator, false);

  }

  private Stream<CsvRecordHeaderValues> fetchCsvRecordHeaderValues_WithoutCheck()
      throws CsvReaderException {

    Iterator<CSVRecord> iterator = fetchCsvRecords().iterator();
    Set<String> headers = csvReaderParameters.getHeaders();

    CsvRecordHeaderValuesIterator csvRecordHeaderValuesIterator = new CsvRecordHeaderValuesIterator(
        iterator, headers);

    return convertIteratorToStream(csvRecordHeaderValuesIterator);

  }

  private Iterable<CSVRecord> fetchCsvRecords()
      throws CsvReaderException {

    try {
      return fetchCsvRecords_WithoutManagementException(reader);
    } catch (IOException e) {
      throw new CsvReaderException(e);
    }


  }

  private Iterable<CSVRecord> fetchCsvRecords_WithoutManagementException(Reader reader)
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

  private Reader createReader(CsvReaderParameters csvReaderParameters) throws CsvReaderException {

    try {
      Path path = csvReaderParameters.getPath();
      return Files.newBufferedReader(path);
    } catch (IOException e) {
      throw new CsvReaderException(e);
    }

  }


  @Override
  public void close() throws IOException {
    if (reader != null) {
      reader.close();
    }
  }
}
