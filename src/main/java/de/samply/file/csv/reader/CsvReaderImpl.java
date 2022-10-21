package de.samply.file.csv.reader;

import de.samply.file.csv.CsvRecordHeaderValues;
import de.samply.file.csv.CsvRecordHeaderValuesIterator;
import de.samply.utils.Constants;
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
import org.apache.commons.csv.CSVFormat.Builder;
import org.apache.commons.csv.CSVRecord;

public class CsvReaderImpl implements CsvReader {


  private final Reader reader;
  private final CsvReaderParameters csvReaderParameters;
  private String delimiter = Constants.DEFAULT_DELIMITER;

  public CsvReaderImpl(CsvReaderParameters csvReaderParameters) throws CsvReaderException {
    this.csvReaderParameters = csvReaderParameters;
    this.reader = createReader(csvReaderParameters);
  }

  /**
   * Fetch values of csv records for specified headers.
   *
   * @return stream of header-values of csv records.
   * @throws CsvReaderException exception that encapsulates all exceptions within the class.
   */
  @Override
  public Stream<CsvRecordHeaderValues> readCsvRecordHeaderValues() throws CsvReaderException {

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

    Builder builder = Builder.create();
    if (!csvReaderParameters.readAllHeaders()) {
      builder.setHeader(csvReaderParameters.getHeaders().toArray(new String[0]));
    } else{
      builder.setHeader();
    }

    return builder.setSkipHeaderRecord(true)
        .setIgnoreEmptyLines(true)
        .setIgnoreHeaderCase(true)
        .setDelimiter(delimiter)
        .build().parse(reader);

  }

  private Reader createReader(CsvReaderParameters csvReaderParameters) throws CsvReaderException {

    try {
      Path path = csvReaderParameters.getPath();
      return Files.newBufferedReader(path);
    } catch (IOException e) {
      throw new CsvReaderException(e);
    }

  }

  /**
   * Set delimiter of csv file.
   *
   * @param delimiter Csv file delimiter.
   */
  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  @Override
  public void close() throws IOException {
    if (reader != null) {
      reader.close();
    }
  }

}
