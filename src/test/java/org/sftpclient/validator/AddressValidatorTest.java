package org.sftpclient.validator;

import org.sftpclient.model.Address;
import org.sftpclient.model.ValidationResult;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
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


    @Test
    public void shouldAcceptValidIp() {
        Address address = new Address("example.com", "1.1.1.1");

        ValidationResult result = validator.validate(Collections.emptyList(), address);

        Assert.assertTrue(result.isValid(), "Ip should be valid");
    }

    @Test
    public void shouldRejectIp_WhenOctetGreaterThan255() {
        Address address = new Address("example.com", "256.1.1.1");

        ValidationResult result = validator.validate(Collections.emptyList(), address);

        Assert.assertFalse(result.isValid(), "Ip when octet greater than 255 should be rejected");
    }

    @Test
    public void shouldRejectIp_WhenContainsLetters() {
        Address address = new Address("example.com", "1.1.1.abc");

        ValidationResult result = validator.validate(Collections.emptyList(), address);

        Assert.assertFalse(result.isValid(), "Ip contains illegal characters");
    }

    @Test
    public void shouldRejectIp_WhenLessThanFourParts() {
        Address address = new Address("example.com", "256.1.1");

        ValidationResult result = validator.validate(Collections.emptyList(), address);

        Assert.assertFalse(result.isValid(), "IP contains less than 4 parts, should be rejected");
    }

    @Test
    public void shouldRejectIp_WhenMoreThanFourParts() {
        Address address = new Address("example.com", "256.1.1.1.1");

        ValidationResult result = validator.validate(Collections.emptyList(), address);

        Assert.assertFalse(result.isValid(), "Ip contains more than 4 parts, should be rejected");
    }

    @Test
    public void shouldRejectIp_WhenIpIsEmpty() {
        Address address = new Address("example.com", "");

        ValidationResult result = validator.validate(Collections.emptyList(), address);

        Assert.assertFalse(result.isValid(), "Empty ip should be rejected");
    }

    @Test
    public void shouldRejectIp_WhenIpIsNull() {
        Address address = new Address("example.com", null);

        ValidationResult result = validator.validate(Collections.emptyList(), address);

        Assert.assertFalse(result.isValid(), "Ip null should be rejected");
    }

    @Test
    public void shouldRejectIp_WhenIpAlreadyExists() {
        Address address = new Address("example.com", "1.1.1.1");
        List<Address> addresses = Arrays.asList(
                new Address("test1.com", "1.1.1.1"),
                new Address("test2.com", "2.2.2.2"));

        ValidationResult result = validator.validate(addresses, address);

        Assert.assertFalse(result.isValid(), "Address with duplicate IP should be rejected");
    }

    @Test
    public void shouldAcceptValidDomain() {
        Address address = new Address("example.com", "1.1.1.1");

        ValidationResult result = validator.validate(Collections.emptyList(), address);

        Assert.assertTrue(result.isValid(), "Domain should be valid");
    }

    @Test
    public void shouldRejectDomain_WhenDomainIsEmpty() {
        Address address = new Address("", "1.1.1.1");

        ValidationResult result = validator.validate(Collections.emptyList(), address);

        Assert.assertFalse(result.isValid(), "Empty domain should be rejected");
    }

    @Test
    public void shouldRejectDomain_WhenDomainIsNull() {
        Address address = new Address("example.com", null);

        ValidationResult result = validator.validate(Collections.emptyList(), address);

        Assert.assertFalse(result.isValid(), "Domain is null should be rejected");
    }

    @Test
    public void shouldRejectDomain_WhenDomainAlreadyExists() {
        Address address = new Address("test2.com", "1.1.1.1");
        List<Address> addresses = Arrays.asList(
                new Address("test1.com", "1.1.1.1"),
                new Address("test2.com", "2.2.2.2"));

        ValidationResult result = validator.validate(addresses, address);

        Assert.assertFalse(result.isValid(), "Address with duplicate domain should be rejected");
    }
}
