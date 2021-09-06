package de.samply.file.bundle;

import de.samply.file.ftp.FtpServerConfig;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import org.apache.commons.net.ftp.FTPClient;

public class FtpPathsBundleManager extends PathsBundleManagerImpl implements PathsBundleManager {

  private FtpServerConfig ftpServerConfig;

  /**
   * Moves paths bundle to external directory through FTP.
   *
   * @param inputFolderPath input folder path.
   */
  public FtpPathsBundleManager(String inputFolderPath, FtpServerConfig ftpServerConfig) {
    super(inputFolderPath, null);
    this.ftpServerConfig = ftpServerConfig;

  }

  @Override
  protected void moveFileToOutputFolder(Path path) throws PathsBundleManagerException {

    try (FtpClientCloseable ftpClientCloseable = new FtpClientCloseable()) {
      moveFileToOutputFolder(ftpClientCloseable.getFtpClient(), path);
    } catch (IOException e) {
      throw new PathsBundleManagerException(e);
    }

  }

  private void moveFileToOutputFolder(FTPClient ftpClient, Path path)
      throws IOException {

    //TODO
    String remoteUrl = null;
    InputStream local = null;
    ftpClient.storeFile(remoteUrl, local);

  }

  private String getRemoteUrl() {
    //TODO
    return null;
  }

  private class FtpClientCloseable implements Closeable {

    FTPClient ftpClient;

    public FtpClientCloseable() throws IOException {
      this.ftpClient = new FTPClient();
      connect();
      login();
    }

    private void connect() throws IOException {
      ftpClient.connect(ftpServerConfig.getHostname(), ftpServerConfig.getPort());
    }

    private void login() throws IOException {
      ftpClient.login(ftpServerConfig.getUser(), ftpServerConfig.getPassword());
    }

    public FTPClient getFtpClient() {
      return ftpClient;
    }

    @Override
    public void close() throws IOException {
      if (ftpClient != null && ftpClient.isConnected()) {
        ftpClient.disconnect();
      }
    }

  }

}
