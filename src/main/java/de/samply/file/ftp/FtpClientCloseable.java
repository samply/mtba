package de.samply.file.ftp;

import java.io.Closeable;
import java.io.IOException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPHTTPClient;

public class FtpClientCloseable implements Closeable {

  private FTPClient ftpClient;
  private FtpServerConfig ftpServerConfig;

  /**
   * Wraps a ftp client as closeable.
   * @param ftpServerConfig ftp server configuration.
   * @throws IOException IO Exception in FTP connection.
   */
  public FtpClientCloseable(FtpServerConfig ftpServerConfig) throws IOException {

    this.ftpServerConfig = ftpServerConfig;
    this.ftpClient = generateFtpClient();
    connect();
    login();

  }

  private FTPClient generateFtpClient() {

    FTPClient ftpClient;

    ProxyConfig proxyConfig = ftpServerConfig.getProxyConfig();
    if (proxyConfig != null) {

      String proxyHost = proxyConfig.getProxyHost();
      int proxyPort = proxyConfig.getProxyPort();
      String proxyUser = proxyConfig.getProxyUser();
      String proxyPass = proxyConfig.getProxyPassword();

      ftpClient = new FTPHTTPClient(proxyHost, proxyPort, proxyUser, proxyPass);

    } else {
      ftpClient = new FTPClient();
    }

    return ftpClient;

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
