package de.samply.file.bundle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class PathsBundleManagerImpl implements PathsBundleManager {

  protected Path inputFolderPath;
  protected Path outputFolderPath;

  /**
   * The current implementation assumes that there is only one bundle in the whole directory.
   *
   * @param inputFolderPath  input folder path.
   * @param outputFolderPath output folder path.
   */
  public PathsBundleManagerImpl(String inputFolderPath, String outputFolderPath) {
    this.inputFolderPath = Paths.get(inputFolderPath);
    this.outputFolderPath = Paths.get(outputFolderPath);
  }

  @Override
  public boolean isNextPathsBundleInInputFolder() throws PathsBundleManagerException {
    try {
      return isNextPathsBundleInInputFolder_WithoutManagementException();
    } catch (IOException e) {
      throw new PathsBundleManagerException(e);
    }
  }

  private boolean isNextPathsBundleInInputFolder_WithoutManagementException() throws IOException {
    return inputFolderPath != null && Files.isDirectory(inputFolderPath)
        && Files.list(inputFolderPath).iterator().hasNext();
  }

  @Override
  public PathsBundle fetchNextPathsBundleFromInputFolder() throws PathsBundleManagerException {
    try {
      return fetchNextPathsBundleFromInputFolder_WithoutManagementException();
    } catch (IOException e) {
      throw new PathsBundleManagerException(e);
    }
  }

  private PathsBundle fetchNextPathsBundleFromInputFolder_WithoutManagementException()
      throws PathsBundleManagerException, IOException {

    PathsBundle pathsBundle = null;

    if (isNextPathsBundleInInputFolder()) {

      pathsBundle = new PathsBundle();
      PathsBundle finalPathsBundle = pathsBundle;
      Files.list(inputFolderPath).forEach(path -> finalPathsBundle.addPath(path));

    }

    return pathsBundle;

  }

  @Override
  public void movePathsBundleToOutputFolder(PathsBundle pathsBundle)
      throws PathsBundleManagerException {

    if (pathsBundle != null) {
      pathsBundle.applyToAllPaths(path -> moveFileToOutputFolder(path));
    }

  }

  protected void moveFileToOutputFolder(Path path) throws PathsBundleManagerException {
    try {
      Files.move(path, outputFolderPath.resolve(path.getFileName()),
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new PathsBundleManagerException(e);
    }
  }

}
