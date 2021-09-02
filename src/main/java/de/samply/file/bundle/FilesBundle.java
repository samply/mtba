package de.samply.file.bundle;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FilesBundle {

  private Map<String, File> files = new HashMap<>();

  /**
   * Add file to bundle.
   * @param file file to be added.
   */
  public void addFile(File file) {

    if (file != null && file.exists()) {
      files.put(file.getName(), file);
    }

  }

  /**
   * Get all files of bundle.
   * @return all files of bundle.
   */
  public Collection<File> getAllFiles() {
    return files.values();
  }

  /**
   * Get file in bundle by filename.
   * @param filename filename to identify file.
   * @return requested file.
   */
  public File getFile(String filename) {
    return files.get(filename);
  }

}
