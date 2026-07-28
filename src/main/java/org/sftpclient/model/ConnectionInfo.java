package org.sftpclient.model;

public class ConnectionInfo {
    private final String host;
    private final int port;
    private final String login;
    private final String password;

    public ConnectionInfo(String host, int port, String login, String password) {
        this.host = host;
        this.port = port;
        this.login = login;
        this.password = password;

    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
