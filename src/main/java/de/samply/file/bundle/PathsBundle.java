package de.samply.file.bundle;

import de.samply.utils.EitherUtils;
import de.samply.utils.EitherUtils.ThrowingConsumer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathsBundle {


  private Path directory;
  private final Logger logger = LoggerFactory.getLogger(PathsBundle.class);
  private final Map<String, Path> pathMap = new HashMap<>();

  /**
   * Add Paths to Paths Bundle.
   *
   * @param paths Paths to be added.
   */
  public void addPaths(Collection<Path> paths) {
    for (Path path : paths) {
      addPath(path);
    }
  }

  /**
   * Add path to bundle.
   *
   * @param path path to be added.
   */
  public void addPath(Path path) {
    try {
      addPathWithoutManagementException(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void addPathWithoutManagementException(Path path) throws IOException {

    if (path != null) {
      if (directory != null && path.getParent() == null) {
        path = directory.resolve(Paths.get(path.getFileName().toString()));
      }
      if (Files.isDirectory(path)) {
        Files.list(path).forEach(tempPath -> addPath(tempPath));
      } else {
        pathMap.put(path.getFileName().toString(), path);
        setDirectory(path);
      }
    }

  }

  /**
   * Set directory of paths bundle.
   *
   * @param path Directory of paths bundle.
   */
  public void setDirectory(Path path) {
    if (path != null && Files.exists(path.getParent())) {
      if (directory == null || !directory.equals(path.getParent()) && directory.startsWith(
          path.getParent())) {
        directory = path.getParent();
        pathMap.values().forEach(tempPath -> {
          if (tempPath.getParent() == null) {
            pathMap.put(tempPath.getFileName().toString(), directory.resolve(tempPath));
          }
        });
      }
    }
  }

  /**
   * Get Directory of Paths Bundle.
   *
   * @return directory of paths bundle.
   */
  public Path getDirectory() {
    return directory;
  }

  /**
   * Get all paths of bundle.
   *
   * @return all paths of bundle.
   */
  public Collection<Path> getAllPaths() {
    return pathMap.values();
  }

  /**
   * Get path in bundle by filename.
   *
   * @param filename filename to identify path.
   * @return requested path.
   */
  public Path getPath(String filename) {
    return pathMap.get(filename);
  }

  /**
   * Apply FileConsumer to file filename.
   *
   * @param pathConsumer file consumer.
   * @param filename     filename.
   */
  public <E extends Exception> void applyToPath(ThrowingConsumer<Path, E> pathConsumer,
      String filename)
      throws PathsBundleException {
    try {
      applyToPath_WithoutManagementException(pathConsumer, filename);
    } catch (Exception e) {
      throw new PathsBundleException(e);
    }
  }

  private <E extends Exception> void applyToPath_WithoutManagementException(
      ThrowingConsumer<Path, E> pathConsumer, String filename)
      throws Exception {
    Path path = getPath(filename);
    if (path != null && pathConsumer != null) {
      pathConsumer.accept(path);
    }
  }


  /**
   * Apply path consumer to all paths.
   *
   * @param pathConsumer path consumer
   */
  public <E extends Exception> void applyToAllPaths(ThrowingConsumer<Path, E> pathConsumer) {

    if (pathConsumer != null) {
      getAllPaths()
          .stream()
          .map(EitherUtils.liftConsumer(path -> pathConsumer.accept(path)))
          .filter(Objects::nonNull)
          .forEach(either -> logger
              .error("Exception while applying consumer to file", (Exception) either.getLeft()));
    }

  }

  public boolean isPathBundleEmpty() {
    return pathMap.isEmpty();
  }

  public PathsBundle clone() {
    PathsBundle pathsBundle = new PathsBundle();

    pathsBundle.setDirectory(this.directory);
    pathsBundle.addPaths(this.getAllPaths());

    return pathsBundle;
  }

}
