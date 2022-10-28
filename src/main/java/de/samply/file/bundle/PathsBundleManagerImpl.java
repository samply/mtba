package de.samply.file.bundle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class PathsBundleManagerImpl implements PathsBundleManager {

  protected Path inputFolderPath;

  /**
   * The current implementation assumes that there is only one bundle in the whole directory.
   *
   * @param inputFolderPath  input folder path.
   */
  public PathsBundleManagerImpl(String inputFolderPath) {
    this.inputFolderPath = Paths.get(inputFolderPath);
  }

  /**
   * The current implementation assumes that there is only one bundle in the whole directory.
   *
   * @param inputFolderPath  input folder path.
   */
  public PathsBundleManagerImpl(Path inputFolderPath) {
    this.inputFolderPath = inputFolderPath;
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

    PathsBundle pathsBundle = new PathsBundle();

    if (isNextPathsBundleInInputFolder()) {
      PathsBundle finalPathsBundle = pathsBundle;
      Files.list(inputFolderPath).forEach(path -> finalPathsBundle.addPath(path));
    }

    return pathsBundle;

  }

  @Override
  public void movePathsBundleToOutputFolder(PathsBundle pathsBundle, Path outputFolderPath)
      throws PathsBundleManagerException {

    if (pathsBundle != null) {
      pathsBundle.applyToAllPaths(path -> moveFileToOutputFolder(path, outputFolderPath));
      pathsBundle.setDirectory(outputFolderPath);
    }

  }

  @Override
  public PathsBundle copyPathsBundleToOutputFolder(PathsBundle pathsBundle, Path outputFolderPath)
      throws PathsBundleManagerException {

    PathsBundle pathsBundle2 = null;
    if (pathsBundle != null) {
      pathsBundle2 = pathsBundle.clone();
      pathsBundle2.applyToAllPaths(path -> copyFileToOutputFolder(path, outputFolderPath));
      pathsBundle2.setDirectory(outputFolderPath);
    }

    return pathsBundle2;

  }

  protected void moveFileToOutputFolder(Path path, Path outputFolderPath) throws PathsBundleManagerException {
    try {
      Files.move(path, outputFolderPath.resolve(path.getFileName()),
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new PathsBundleManagerException(e);
    }
  }

  protected void copyFileToOutputFolder(Path path, Path outputFolderPath) throws PathsBundleManagerException {
    try {
      Files.copy(path, outputFolderPath.resolve(path.getFileName()),
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new PathsBundleManagerException(e);
    }
  }

}
