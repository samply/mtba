package de.samply.file.bundle;

public interface PathsBundleManager {

  boolean isNextPathsBundleInInputFolder() throws PathsBundleException;

  PathsBundle fetchNextPathsBundleFromInputFolder() throws PathsBundleException;

  void movePathsBundleToOutputFolder(PathsBundle pathsBundle) throws PathsBundleException;

}
