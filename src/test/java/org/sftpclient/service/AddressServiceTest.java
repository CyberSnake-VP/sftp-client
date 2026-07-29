package org.sftpclient.service;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.sftpclient.model.Address;
import org.sftpclient.model.ConnectionInfo;
import org.sftpclient.model.ValidationResult;
import org.sftpclient.sftp.SftpClient;
import org.sftpclient.validator.AddressValidator;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AddressServiceTest {

    private SftpClient sftpClient;
    private AddressValidator validator;
    private AddressService addressService;
    private static final String ADDRESS_FILE = "upload/address-test.json";

    @BeforeMethod
    public void setUp() throws JSchException, SftpException {
        sftpClient = new SftpClient(
                new ConnectionInfo(
                        "localhost",
                        2222,
                        "user",
                        "pass"
                ));
        sftpClient.connect();

        // Отправляем файл из resources на сервер для тестирования
        try (InputStream in = getClass()
                .getClassLoader()
                .getResourceAsStream("address-test.json")) {

            sftpClient.uploadFile(in, ADDRESS_FILE);

        } catch (IOException e) {
            throw new RuntimeException("Не загрузить тестовые данные", e);
        }

        validator = new AddressValidator();

        addressService = new AddressService(
                sftpClient,
                validator,
                ADDRESS_FILE
        );

    }

    @AfterMethod
    public void tearDown() {
        sftpClient.disconnect();
    }

    /// Получение списка адресов с сортировкой по домену
    @Test
    public void shouldLoadAddressSortedByDomain() throws SftpException, IOException {
        List<Address> addresses = addressService.getAddresses();
        // получаем список доменов из адресов
        List<String> domains = addresses.stream()
                .map(Address::getDomain)
                .collect(Collectors.toList());

        // Сортируем этот список, сортированный список должен быть равен.
        List<String> sorted = new ArrayList<>(domains);
        Collections.sort(sorted);

        Assert.assertEquals(
                domains,
                sorted,
                "Domains are not sorted correctly"
        );
    }

    /// Получение домена по IP
    @Test
    public void shouldFindDomainByIp() throws SftpException, IOException {
        String expected = "google.com";

        String actual = addressService.findDomainByIp("8.8.8.8");

        Assert.assertEquals(
                actual,
                expected,
                "Domain should be found"
        );
    }

    /// NULL при поиске с пустым ip
    @Test
    public void shouldReturnNull_whenIpEmpty() throws SftpException, IOException {
        String actual = addressService.findDomainByIp("");

        Assert.assertNull(actual, "Domain should be null");
    }

    /// NULL при поиске, когда ip null
    @Test
    public void shouldReturnNull_whenIpNull() throws SftpException, IOException {
        String actual = addressService.findDomainByIp(null);
        Assert.assertNull(actual, "Domain should be null");
    }

    /// Поиск IP по домену
    @Test
    public void shouldFindIpByDomain() throws SftpException, IOException {
        String expected = "8.8.8.8";

        String actual = addressService.findIpByDomain("google.com");

        Assert.assertEquals(
                actual,
                expected,
                "Ip should be found"
        );
    }

    /// NULL при поиске с пустым доменом
    @Test
    public void shouldReturnNull_WhenDomainIsEmpty() throws SftpException, IOException {
        String actual = addressService.findIpByDomain("");
        Assert.assertNull(actual, "Domain should be null");
    }

    /// NULL при поиске, когда домен NULL
    @Test
    public void shouldReturnNull_WhenDomainIsNull() throws SftpException, IOException {
        String actual = addressService.findIpByDomain(null);
        Assert.assertNull(actual, "Domain should be null");
    }

    /// Добавление адреса в список
    @Test
    public void shouldAddAddress() throws SftpException, IOException {
        ValidationResult result =
                addressService.addAddress("test.com", "1.2.3.4");

        Assert.assertTrue(result.isValid(),
                "Address should be added"
        );

        String domainResult = addressService.findDomainByIp("1.2.3.4");
        List<Address> addresses = addressService.getAddresses();

        // Проверка, что найденный домен соответствует добавленному
        Assert.assertEquals(
                domainResult,
                "test.com",
                "Domain is not equals"
        );
        // Проверяем список адресов на наличие нужного адреса(домен)
        Assert.assertTrue(
                addresses.stream()
                        .anyMatch(a -> "test.com".equals(a.getDomain()))
        );
    }

    /// Удаление адреса по домену
    @Test
    public void shouldRemoveAddress_ByDomain() throws SftpException, IOException {
        String ip = addressService.findIpByDomain("google.com");
        Assert.assertEquals(ip, "8.8.8.8");

        ValidationResult result =
                addressService.deleteAddress("google.com");
        Assert.assertTrue(result.isValid(), "Address should be removed");

        String deleteIp = addressService.findIpByDomain("google.com");
        Assert.assertNull(deleteIp, "Ip address should be null");
    }

    /// Удаление адреса по ip
    @Test
    public void shouldRemoveAddress_ByIp() throws SftpException, IOException {
        String domain = addressService.findDomainByIp("8.8.8.8");
        Assert.assertEquals(domain, "google.com");

        ValidationResult result =
                addressService.deleteAddress("8.8.8.8");
        Assert.assertTrue(result.isValid(), "Address should be removed");

        String deleteDomain = addressService.findDomainByIp("8.8.8.8");
        Assert.assertNull(deleteDomain, "Domain should be null");
    }


    /// NULL при удалении, когда домен NULL
    @Test
    public void shouldRemoveAddress_ByDomainAndIp() throws SftpException, IOException {
        String domain = addressService.findDomainByIp("8.8.8.8");
        Assert.assertEquals(domain, "google.com");

        ValidationResult result =
                addressService.deleteAddress(null);
        Assert.assertFalse(result.isValid(), "Should be null");

        String domainExists = addressService.findDomainByIp("8.8.8.8");
        Assert.assertNotNull(domainExists, "Domain should be exists");
    }

    /// Уникальность IP и Домена
    @Test
    public void shouldNotAddAddress_whenIpNotUnique() throws SftpException, IOException {
        String existedIp = addressService.findIpByDomain("google.com");
        Assert.assertNotNull(existedIp, "Ip address should not be null");

        List<Address> before = addressService.getAddresses();
        ValidationResult result =
                addressService.addAddress("notunique.com", existedIp);
        List<Address> after = addressService.getAddresses();

        Assert.assertFalse(result.isValid(), "IP address should be unique");
        Assert.assertEquals(after.size(), before.size(), "Address should be not added");

    }

    @Test
    public void shouldNotAddAddress_whenDomainNotUnique() throws SftpException, IOException {
        String existedDomain = addressService.findDomainByIp("8.8.8.8");
        Assert.assertNotNull(existedDomain, "Domain should not be null");

        List<Address> before = addressService.getAddresses();
        ValidationResult result
                = addressService.addAddress(existedDomain, "1.2.3.4");
        List<Address> after = addressService.getAddresses();

        Assert.assertFalse(result.isValid(), "Domain should be unique");
        Assert.assertEquals(after.size(), before.size(), "Address should be not added");
    }
}
