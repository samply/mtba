package de.samply.file.bundle;

import de.samply.file.ftp.FtpClientCloseable;
import de.samply.file.ftp.FtpServerConfig;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.net.ftp.FTPClient;

public class FtpPathsBundleManager extends PathsBundleManagerImpl implements PathsBundleManager {

  private FtpServerConfig ftpServerConfig;

  /**
   * Moves paths bundle to external directory through FTP.
   *
   * @param inputFolderPath  source input folder path.
   * @param ftpServerConfig  ftp server configuration.
   */
  public FtpPathsBundleManager(String inputFolderPath, FtpServerConfig ftpServerConfig) {
    super(inputFolderPath);
    this.ftpServerConfig = ftpServerConfig;

  }

  @Override
  protected Path moveFileToOutputFolder(PathsBundle pathsBundle, Path path, Path outputFolderPath) throws PathsBundleManagerException {

    try (FtpClientCloseable ftpClientCloseable = new FtpClientCloseable(ftpServerConfig)) {
      Path tempPath = pathsBundle.getDirectory().relativize(path);
      moveFileToOutputFolder(ftpClientCloseable.getFtpClient(), tempPath, outputFolderPath);
      return outputFolderPath.resolve(tempPath);
    } catch (IOException e) {
      throw new PathsBundleManagerException(e);
    }

  }

  private void moveFileToOutputFolder(FTPClient ftpClient, Path path, Path outputFolderPath)
      throws PathsBundleManagerException {

    try (InputStream pathInputStream = Files.newInputStream(path)) {
      moveFileToOutputFolder(ftpClient, pathInputStream, outputFolderPath);
    } catch (IOException e) {
      throw new PathsBundleManagerException(e);
    }

  }

  private void moveFileToOutputFolder(FTPClient ftpClient, InputStream pathInputStream, Path outputFolderPath)
      throws IOException {
    ftpClient.storeFile(outputFolderPath.toString(), pathInputStream);
  }

  @Override
  protected void copyFileToOutputFolder(PathsBundle pathsBundle, Path path, Path outputFolderPath) throws PathsBundleManagerException {
    moveFileToOutputFolder(pathsBundle, path, outputFolderPath);
  }
}
