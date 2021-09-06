package de.samply.file.bundle;

public interface PathsBundleManager {

  boolean isNextPathsBundleInInputFolder() throws PathsBundleManagerException;

  PathsBundle fetchNextPathsBundleFromInputFolder() throws PathsBundleManagerException;

  void movePathsBundleToOutputFolder(PathsBundle pathsBundle) throws PathsBundleManagerException;

}
