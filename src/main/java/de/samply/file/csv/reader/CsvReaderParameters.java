package de.samply.file.csv.reader;

import de.samply.file.bundle.PathsBundle;
import java.util.HashSet;
import java.util.Set;

public class CsvReaderParameters {

  private Set<String> headers = new HashSet<>();
  private String filename;
  private PathsBundle pathsBundle;

  public Set<String> getHeaders() {
    return headers;
  }

  public void addHeader(String header) {
    headers.add(header);
  }

  public void setHeaders(Set<String> headers) {
    this.headers = headers;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public PathsBundle getPathsBundle() {
    return pathsBundle;
  }

  public void setPathsBundle(PathsBundle pathsBundle) {
    this.pathsBundle = pathsBundle;
  }

}
