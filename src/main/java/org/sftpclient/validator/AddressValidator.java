package org.sftpclient.validator;

import org.sftpclient.model.Address;
import org.sftpclient.model.ValidationResult;

import java.util.List;

/// Проверяем валидность адресов
public class AddressValidator {

    public ValidationResult validate(List<Address> addressList,
                                            Address address) {

        String ip = address.getIp();
        String domain = address.getDomain();


        /// Проверка на существование
        if (ip == null || ip.trim().isEmpty()) {
            return new ValidationResult(false, "IP address cannot be empty");
        }
        if (domain == null || domain.trim().isEmpty()) {
            return new ValidationResult(false, "Domain address cannot be empty");
        }
        /// Валидность ip
        if (!isValidIp(ip)) {
            return new ValidationResult(false, "Invalid IPv4 address");
        }
        /// Проверка на уникальность
        if(ipExists(ip, addressList)){
             return new ValidationResult(false, "IP address is not unique");
        }
        if(domainExists(domain, addressList)) {
            return new ValidationResult(false, "Domain address is not unique");
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

    private boolean ipExists(String ip,  List<Address> addressList) {
        return addressList.stream()
                .anyMatch(address -> ip.equals(address.getIp()));
    }

    private boolean domainExists(String domain, List<Address> addressList) {
        return  addressList.stream()
                .anyMatch(address -> domain.equals(address.getDomain()));
    }
}
