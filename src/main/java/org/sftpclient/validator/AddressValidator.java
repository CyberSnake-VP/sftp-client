package org.sftpclient.validator;

import org.sftpclient.model.Address;
import org.sftpclient.model.ValidationResult;

import java.util.List;

/// Проверяем валидность адресов
public class AddressValidator {

    public ValidationResult validate(List<Address> addressList,
                                     Address address) {

        ValidationResult result = validateIp(address.getIp());
        if (!result.isValid()) {
            return result;
        }

        result = validateDomain(address.getDomain());
        if (!result.isValid()) {
            return result;
        }

        return validateUnique(addressList, address);
    }


    // Валидация IP
    private ValidationResult validateIp(String ip) {
        /// Проверка на существование
        if (ip == null || ip.trim().isEmpty()) {
            return new ValidationResult(false, "IP address cannot be empty");
        }
        /// Валидность ip
        if (!isValidIp(ip)) {
            return new ValidationResult(false, "Invalid IPv4 address");
        }
        return new ValidationResult(true, "");
    }

    // Валидация Домена
    private ValidationResult validateDomain(String domain) {
        if (domain == null || domain.trim().isEmpty()) {
            return new ValidationResult(false, "Domain address cannot be empty");
        }
        return new ValidationResult(true, "");
    }

    // Проверка на уникальность
    private ValidationResult validateUnique(List<Address> addressList, Address address) {
        for (Address a : addressList) {
            if (address.getDomain().equals(a.getDomain())) {
                return new ValidationResult(false, "Domain already exists");
            }

            if (address.getIp().equals(a.getIp())) {
                return new ValidationResult(false, "Ip already exists");
            }
        }
        return new ValidationResult(true, "");
    }


    /// Проверяем ip на корректность
    private boolean isValidIp(String ip) {
        // Экранируем точку и сам слеш))
        String[] parts = ip.split("\\.");

        // ip имеет 4 части
        if (parts.length != 4) {
            return false;
        }

        // Каждая часть должна быть числом, и подходить под условие
        try {
            for (String part : parts) {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
