package org.sftpclient.validator;

import org.sftpclient.model.Address;
import org.sftpclient.model.ValidationResult;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class AddressValidatorTest {
    private AddressValidator validator;


    @BeforeMethod
    public void setUp() {
        validator = new AddressValidator();
    }

    // Организуем проверку валидности ip, разные входные данные, результат всегда один.
    @DataProvider(name = "invalidIps")
    public Object[][] invalidIps() {
        return new Object[][]{
                {"256.1.1.1", "octet greater than 255"},
                {"255.1.1.1.1", "more than four parts"},
                {"255.1.1", "less than four parts"},
                {"255.1.1.abc", "contains letters"}
        };
    }
    /// Благодаря такому подходу, нам нужно написать один метод, где будут подставлены значения из DataProvider
    @Test(dataProvider = "invalidIps")
    public void shouldRejectIp_WhenIpIsInvalid(String invalidIp, String description) {
        Address address = new Address("example.com", invalidIp);

        ValidationResult result = validator.validate(Collections.emptyList(), address);

        Assert.assertFalse(result.isValid(), description);
        Assert.assertEquals(result.getMessage(), "Invalid IPv4 address");
    }

    @Test
    public void shouldAcceptValid() {
        Address address = new Address("example.com", "1.1.1.1");

        ValidationResult result = validator.validate(Collections.emptyList(), address);

        Assert.assertTrue(result.isValid(), "Ip should be valid");
    }

    @Test
    public void shouldRejectIp_WhenIpIsEmpty() {
        Address address = new Address("example.com", "");

        ValidationResult result = validator.validate(Collections.emptyList(), address);

        Assert.assertFalse(result.isValid(), "Empty ip should be rejected");
        Assert.assertEquals(result.getMessage(), "IP address cannot be empty");
    }

    @Test
    public void shouldRejectIp_WhenIpIsNull() {
        Address address = new Address("example.com", null);

        ValidationResult result = validator.validate(Collections.emptyList(), address);

        Assert.assertFalse(result.isValid(), "Ip null should be rejected");
        Assert.assertEquals(result.getMessage(), "IP address cannot be empty");
    }

    @Test
    public void shouldRejectAddress_WhenIpAlreadyExists() {
        Address address = new Address("example.com", "1.1.1.1");
        List<Address> addresses = Arrays.asList(
                new Address("test1.com", "1.1.1.1"),
                new Address("test2.com", "2.2.2.2"));

        ValidationResult result = validator.validate(addresses, address);

        Assert.assertFalse(result.isValid(), "Address with duplicate IP should be rejected");
        Assert.assertEquals(result.getMessage(), "Ip already exists");
    }



    @Test
    public void shouldRejectDomain_WhenDomainIsEmpty() {
        Address address = new Address("", "1.1.1.1");

        ValidationResult result = validator.validate(Collections.emptyList(), address);

        Assert.assertFalse(result.isValid(), "Empty domain should be rejected");
        Assert.assertEquals(result.getMessage(), "Domain address cannot be empty");
    }

    @Test
    public void shouldRejectDomain_WhenDomainIsNull() {
        Address address = new Address(null, "1.1.1.1");

        ValidationResult result = validator.validate(Collections.emptyList(), address);

        Assert.assertFalse(result.isValid(), "Domain is null should be rejected");
        Assert.assertEquals(result.getMessage(), "Domain address cannot be empty");
    }

    @Test
    public void shouldRejectAddress_WhenDomainAlreadyExists() {
        Address address = new Address("test2.com", "1.2.3.4");
        List<Address> addresses = Arrays.asList(
                new Address("test1.com", "1.1.1.1"),
                new Address("test2.com", "2.2.2.2"));

        ValidationResult result = validator.validate(addresses, address);

        Assert.assertFalse(result.isValid(), "Address with duplicate domain should be rejected");
        Assert.assertEquals(result.getMessage(), "Domain already exists");
    }


}
