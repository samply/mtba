package de.samply.file.bundle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.util.FileSystemUtils;

public class PathsBundleManagerImpl implements PathsBundleManager {

  protected Path inputFolderPath;

  /**
   * The current implementation assumes that there is only one bundle in the whole directory.
   *
   * @param inputFolderPath input folder path.
   */
  public PathsBundleManagerImpl(String inputFolderPath) {
    this.inputFolderPath = Paths.get(inputFolderPath);
  }

  /**
   * The current implementation assumes that there is only one bundle in the whole directory.
   *
   * @param inputFolderPath input folder path.
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
      Path oldDirectory = pathsBundle.getDirectory();
      pathsBundle.applyToAllPaths(
          path -> pathsBundle.addPath(moveFileToOutputFolder(pathsBundle, path, outputFolderPath)));
      pathsBundle.setDirectory(outputFolderPath);
      removeSubdirectories(oldDirectory);
    }

  }

  private void removeSubdirectories(Path directory) throws PathsBundleManagerException {
    try {
      removeSubdirectoriesWithoutExceptionManagement(directory);
    } catch (IOException | RuntimeException e) {
      throw new PathsBundleManagerException(e);
    }
  }

  private void removeSubdirectoriesWithoutExceptionManagement(Path directory) throws IOException {
    if (Files.isDirectory(directory)) {
      Files.list(directory).filter(Files::isDirectory).forEach(path -> {
        try {
          FileSystemUtils.deleteRecursively(path);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }

  @Override
  public PathsBundle copyPathsBundleToOutputFolder(PathsBundle pathsBundle, Path outputFolderPath)
      throws PathsBundleManagerException {

    PathsBundle pathsBundle2 = null;
    if (pathsBundle != null) {
      pathsBundle2 = pathsBundle.clone();
      pathsBundle2.applyToAllPaths(
          path -> copyFileToOutputFolder(pathsBundle, path, outputFolderPath));
      pathsBundle2.setDirectory(outputFolderPath);
    }

    return pathsBundle2;

  }

  protected Path moveFileToOutputFolder(PathsBundle pathsBundle, Path path, Path outputFolderPath)
      throws PathsBundleManagerException {
    try {
      Path tempPath = pathsBundle.getDirectory().relativize(path);
      Path tempPath2 = Paths.get(outputFolderPath.toString(), tempPath.toString());
      if (!Files.exists(tempPath2.getParent())) {
        Files.createDirectory(tempPath2.getParent());
      }
      Files.move(path, tempPath2, StandardCopyOption.REPLACE_EXISTING);
      return tempPath2;
    } catch (IOException e) {
      throw new PathsBundleManagerException(e);
    }
  }

  protected void copyFileToOutputFolder(PathsBundle pathsBundle, Path path, Path outputFolderPath)
      throws PathsBundleManagerException {
    try {
      Path tempPath = pathsBundle.getDirectory().relativize(path);
      Path tempPath2 = Paths.get(outputFolderPath.toString(), tempPath.toString());
      if (!Files.exists(tempPath2.getParent())) {
        Files.createDirectory(tempPath2.getParent());
      }
      Files.copy(path, tempPath2, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new PathsBundleManagerException(e);
    }
  }

}
