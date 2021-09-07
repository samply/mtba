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
   * @param inputFolderPath input folder path.
   */
  public FtpPathsBundleManager(String inputFolderPath, String outputFolderPath,
      FtpServerConfig ftpServerConfig) {
    super(inputFolderPath, outputFolderPath);
    this.ftpServerConfig = ftpServerConfig;

  }

  @Override
  protected void moveFileToOutputFolder(Path path) throws PathsBundleManagerException {

    try (FtpClientCloseable ftpClientCloseable = new FtpClientCloseable(ftpServerConfig)) {
      moveFileToOutputFolder(ftpClientCloseable.getFtpClient(), path);
    } catch (IOException e) {
      throw new PathsBundleManagerException(e);
    }

  }

  private void moveFileToOutputFolder(FTPClient ftpClient, Path path)
      throws PathsBundleManagerException {

    try (InputStream pathInputStream = Files.newInputStream(path)) {
      moveFileToOutputFolder(ftpClient, pathInputStream);
    } catch (IOException e) {
      throw new PathsBundleManagerException(e);
    }

  }

  private void moveFileToOutputFolder(FTPClient ftpClient, InputStream pathInputStream)
      throws IOException {
    ftpClient.storeFile(outputFolderPath.toString(), pathInputStream);
  }

}
