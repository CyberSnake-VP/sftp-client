package org.sftpclient.sftp;

import com.jcraft.jsch.*;
import org.sftpclient.model.ConnectionInfo;

import java.io.InputStream;

public class SftpClient {

    private final String host;
    private final int port;
    private final String login;
    private final String password;

    private Session session;
    private ChannelSftp channel;

    public SftpClient(ConnectionInfo connectionInfo) {
        this.host = connectionInfo.getHost();
        this.port = connectionInfo.getPort();
        this.login = connectionInfo.getLogin();
        this.password = connectionInfo.getPassword();
    }

    public void connect() throws JSchException {
      JSch jsch = new JSch();
      session = jsch.getSession(login, host, port);
      session.setPassword(password);
      // Отключаем проверка ключа, при первом подключении. Для тестового задания.
      session.setConfig("StrictHostKeyChecking", "no");
      session.connect();
      // Создаем подключение к каналу sftp SOfa yor are baby ny soneshine
      createChannel();
    }

    public void disconnect() {
        if (channel != null && channel.isConnected()) {
            channel.disconnect();
        }
        if(session != null && session.isConnected()) {
            session.disconnect();
        }
    }

    private void createChannel() throws JSchException {
      channel = (ChannelSftp) session.openChannel("sftp");
      channel.connect();
    }

    // Отдаем поток к файлу
    public InputStream downloadFile(String path) throws SftpException {
        return channel.get(path);
    }

    // Отправляем поток для записи
    public void uploadFile(InputStream inputStream, String path) throws SftpException {
        channel.put(inputStream, path);
    }
}
