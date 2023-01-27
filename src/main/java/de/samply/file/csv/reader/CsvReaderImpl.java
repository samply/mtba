package de.samply.file.csv.reader;

import de.samply.file.bundle.PathsBundle;
import de.samply.file.csv.CsvRecordHeaderValues;
import de.samply.file.csv.CsvRecordHeaderValuesIterator;
import de.samply.spring.MtbaConst;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
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
  private String delimiter = MtbaConst.DEFAULT_CSV_DELIMITER;
  private String endOfLine = MtbaConst.DEFAULT_END_OF_LINE;
  private Charset charset = MtbaConst.DEFAULT_CHARSET;


  public CsvReaderImpl(String filename, PathsBundle pathsBundle) throws CsvReaderException {
    this(new CsvReaderParameters(filename, pathsBundle));
  }

  public CsvReaderImpl(CsvReaderParameters csvReaderParameters) throws CsvReaderException {
    this.csvReaderParameters = csvReaderParameters;
    setFileConfig(csvReaderParameters);
    this.reader = createReader(csvReaderParameters);
  }

  private void setFileConfig(CsvReaderParameters csvReaderParameters) {
    if (csvReaderParameters.getCharset() != null) {
      this.charset = csvReaderParameters.getCharset();
    }
    if (csvReaderParameters.getDelimiter() != null) {
      this.delimiter = csvReaderParameters.getDelimiter();
    }
    if (csvReaderParameters.getEndOfLine() != null) {
      this.endOfLine = csvReaderParameters.getEndOfLine();
    }
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

    return Builder.create()
        .setHeader()
        .setRecordSeparator(endOfLine)
        .setSkipHeaderRecord(true)
        .setIgnoreEmptyLines(true)
        .setIgnoreHeaderCase(true)
        .setDelimiter(delimiter)
        .build().parse(reader);

  }

  private Reader createReader(CsvReaderParameters csvReaderParameters) throws CsvReaderException {

    try {
      Path path = csvReaderParameters.getPath();
      return Files.newBufferedReader(path, charset);
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
