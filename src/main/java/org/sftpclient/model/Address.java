package org.sftpclient.model;

// Модель соответствует объекту в JSON
public class Address {
    private String domain;
    private String ip;

    public Address(String domain, String ip) {
        this.domain = domain;
        this.ip = ip;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}
