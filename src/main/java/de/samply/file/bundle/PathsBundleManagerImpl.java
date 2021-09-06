package de.samply.file.bundle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class PathsBundleManagerImpl implements PathsBundleManager {

  private Path inputFolderPath;
  private Path outputFolderPath;

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
  public boolean isNextPathsBundleInInputFolder() throws PathsBundleException {
    try {
      return isNextPathsBundleInInputFolder_WithoutManagementException();
    } catch (IOException e) {
      throw new PathsBundleException(e);
    }
  }

  private boolean isNextPathsBundleInInputFolder_WithoutManagementException() throws IOException {
    return inputFolderPath != null && Files.isDirectory(inputFolderPath)
        && Files.list(inputFolderPath).iterator().hasNext();
  }

  @Override
  public PathsBundle fetchNextPathsBundleFromInputFolder() throws PathsBundleException {
    try {
      return fetchNextPathsBundleFromInputFolder_WithoutManagementException();
    } catch (IOException e) {
      throw new PathsBundleException(e);
    }
  }

  private PathsBundle fetchNextPathsBundleFromInputFolder_WithoutManagementException()
      throws PathsBundleException, IOException {

    PathsBundle pathsBundle = null;

    if (isNextPathsBundleInInputFolder()) {

      pathsBundle = new PathsBundle();
      PathsBundle finalPathsBundle = pathsBundle;
      Files.list(inputFolderPath).forEach(path -> finalPathsBundle.addPath(path));

    }

    return pathsBundle;

  }

  @Override
  public void movePathsBundleToOutputFolder(PathsBundle pathsBundle) throws PathsBundleException {

    if (pathsBundle != null) {
      pathsBundle
          .applyToAllPaths(
              path -> Files.move(path, outputFolderPath.resolve(path.getFileName()),
                  StandardCopyOption.REPLACE_EXISTING));
    }

  }


}
