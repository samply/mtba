package de.samply.file.bundle;

import java.nio.file.Path;

public interface PathsBundleManager {

  boolean isNextPathsBundleInInputFolder() throws PathsBundleManagerException;

  PathsBundle fetchNextPathsBundleFromInputFolder() throws PathsBundleManagerException;

  void movePathsBundleToOutputFolder(PathsBundle pathsBundle, Path outputFolderPath) throws PathsBundleManagerException;

  PathsBundle copyPathsBundleToOutputFolder(PathsBundle pathsBundle, Path outputFolderPath) throws PathsBundleManagerException;

}
