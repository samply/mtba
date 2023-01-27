package de.samply.utils;

import de.samply.spring.MtbaConst;
import java.nio.charset.Charset;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileConfig {

  public enum EndOfLine {
    CR("\r"),
    CRLF("\r\n"),
    LF("\n");

    EndOfLine(String value) {
      this.value = value;
    }

    private String value;
  }

  public enum Delimiter {
    TAB("\t");

    Delimiter(String value) {
      this.value = value;
    }

    private String value;
  }

  private String csvDelimiter;
  private String endOfLine;
  private Charset fileCharset;

  public FileConfig(
      @Value(MtbaConst.FILE_END_OF_LINE_SV) String endOfLine,
      @Value(MtbaConst.CSV_DELIMITER_SV) String csvDelimiter,
      @Value(MtbaConst.FILE_CHARSET_SV) String fileCharset) {
    this.endOfLine =
        (endOfLine != null) ? fetchEndOfLine(endOfLine) : MtbaConst.DEFAULT_END_OF_LINE;
    this.csvDelimiter =
        (csvDelimiter != null) ? fetchDelimiter(csvDelimiter) : MtbaConst.DEFAULT_CSV_DELIMITER;
    this.fileCharset =
        (fileCharset != null) ? Charset.forName(fileCharset) : MtbaConst.DEFAULT_CHARSET;
  }

  public String getEndOfLine() {
    return endOfLine;
  }

  public String getCsvDelimiter() {
    return csvDelimiter;
  }

  public Charset getFileCharset() {
    return fileCharset;
  }

  private static String fetchEndOfLine(String endOfLine) {
    try {
      return EndOfLine.valueOf(endOfLine).value;
    } catch (IllegalArgumentException e) {
      return endOfLine;
    }
  }

  private static String fetchDelimiter(String delimiter) {
    try {
      return Delimiter.valueOf(delimiter).value;
    } catch (IllegalArgumentException e) {
      return delimiter;
    }
  }

}
