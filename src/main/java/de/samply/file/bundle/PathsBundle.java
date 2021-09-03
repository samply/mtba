package de.samply.file.bundle;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PathsBundle {

  private final Map<String, Path> pathMap = new HashMap<>();

  /**
   * Add path to bundle.
   *
   * @param path path to be added.
   */
  public void addPath(Path path) {

    if (path != null && Files.exists(path)) {
      pathMap.put(path.getFileName().toString(), path);
    }

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
  public void applyToPath(Consumer<Path> pathConsumer, String filename) {

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
  public void applyToAllPaths(Consumer<Path> pathConsumer) {

    if (pathConsumer != null) {
      for (Path path : getAllPaths()) {
        pathConsumer.accept(path);
      }
    }
  }


}
