package org.sftpclient.model;

import java.util.List;

// Класс - список адресов, его будем десериализовать JSON
public class AddressFile {
    private List<Address> addresses;

    public AddressFile(List<Address> addresses) {
        this.addresses = addresses;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
}
