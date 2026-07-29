package org.sftpclient.service;

import com.google.gson.Gson;
import com.jcraft.jsch.SftpException;
import org.sftpclient.model.Address;
import org.sftpclient.model.AddressFile;
import org.sftpclient.model.ValidationResult;
import org.sftpclient.sftp.SftpClient;
import org.sftpclient.validator.AddressValidator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/// Класс-сервис для работы sftp клиентом, работа с Json адресами
public class AddressService {
    private final SftpClient client;
    private final AddressValidator addressValidator;
    private final String addressPath;


    public AddressService(SftpClient client, AddressValidator addressValidator, String addressPath) {
        this.client = client;
        this.addressValidator = addressValidator;
        this.addressPath = addressPath;
    }

    // Отдаем наш список адресов наружу, использует класс ConsoleMenu
    public List<Address> getAddresses() throws IOException, SftpException {
        return Collections.unmodifiableList(loadAddresses());
    }

    private List<Address> loadAddresses() throws IOException, SftpException {
        // Получаем поток
        try (InputStream inputStream = client.downloadFile(addressPath);
             Reader reader = new InputStreamReader(inputStream)) {
            // Преобразовать JSON в AddressFile
            Gson gson = new Gson();
            AddressFile addressFile = gson.fromJson(reader, AddressFile.class);
            List<Address> addresses = addressFile.getAddresses();
            // Сортируем по доменному имени
            addresses.sort(Comparator.comparing(Address::getDomain));

            return addresses;
        }
    }

    /// Ищем адрес, получаем его ip
    public String findIpByDomain(String domain) throws IOException, SftpException {
        if(domain == null || domain.trim().isEmpty()) {
            return null;
        }

        String normalizedDomain = domain.trim().toLowerCase(Locale.ROOT);
        return loadAddresses()
                .stream()
                .filter(address ->
                        normalizedDomain.equals(address.getDomain()))
                .map(Address::getIp)
                .findFirst()
                .orElse(null);
    }

    /// Ищем адрес и получаем его домен
    public String findDomainByIp(String ip) throws IOException, SftpException {
        if (ip == null || ip.trim().isEmpty()) {
            return null;
        }

        String normalizedIp = ip.trim();

        return loadAddresses()
                .stream()
                .filter(address ->
                        normalizedIp.equals(address.getIp()))
                .map(Address::getDomain)
                .findFirst()
                .orElse(null);
    }

    public ValidationResult addAddress(String domain, String ip) throws IOException, SftpException {
        domain = domain.trim().toLowerCase(Locale.ROOT);
        ip = ip.trim();

        List<Address> addresses = loadAddresses();
        Address address = new Address(domain, ip);

        // Валидируем адрес
        ValidationResult result = addressValidator.validate(addresses, address);

        if (!result.isValid()) {
            // отдадим результат отрицательной валидации вызывающему методу из ConsoleMenu
            return result;
        }
        addresses.add(address);
        saveAddresses(addresses);

        // Так же отдаем результат положительной валидации, теперь метод в ConsoleMenu
        return new ValidationResult(true, "Address added successfully");
    }

    private void saveAddresses(List<Address> addresses) throws SftpException, IOException {
        Gson gson = new Gson();
        AddressFile addressFile = new AddressFile(addresses);
        String json = gson.toJson(addressFile);

        try (InputStream inputStream = new ByteArrayInputStream(
                json.getBytes(StandardCharsets.UTF_8))) {

            client.uploadFile(inputStream, addressPath);
        }
    }

    public ValidationResult deleteAddress(String value) throws IOException, SftpException {
        if (value == null || value.trim().isEmpty()) {
            return new ValidationResult(false, "Value cannot be empty");
        }
        List<Address> addresses = loadAddresses();

        // Приводим к одному стилю, плюс используем локаль для системы
        String normalizedValue = value.trim().toLowerCase(Locale.ROOT);

        boolean removed = addresses.removeIf(
                address -> normalizedValue.equals(address.getIp()) ||
                        normalizedValue.equals(address.getDomain()));
        if (!removed) {
            return new ValidationResult(false, "Address not found");
        }
        saveAddresses(addresses);
        return new ValidationResult(true, "Address successfully deleted");
    }

}
