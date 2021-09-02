package de.samply.file.bundle;

public interface FilesBundleManager {

  boolean isNextFileBundleInInputFolder();

  FilesBundle fetchNextFileBundleFromInputFolder();

  void moveFilesBundleToOutputFolder(FilesBundle filesBundle);

}
