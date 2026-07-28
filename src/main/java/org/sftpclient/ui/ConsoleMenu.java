package org.sftpclient.ui;

import com.jcraft.jsch.SftpException;
import org.sftpclient.model.Address;
import org.sftpclient.model.ValidationResult;
import org.sftpclient.service.AddressService;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {
    private final Scanner scanner;
    private final AddressService addressService;

    public ConsoleMenu(Scanner scanner,  AddressService addressService) {
        this.scanner = scanner;
        this.addressService = addressService;
    }

    /// Основной метод класса для работы с меню
    public void start() throws SftpException, IOException {
        while (true) {
            showMenu();
            // Получаем команду от пользователя
            int command = readCommand();

            switch (command) {
                case 1:
                    showAddresses();
                    break;
                case 2:
                    showIpByDomain();
                    break;
                case 3:
                    showDomainByIp();
                    break;
                case 4:
                    addAddress();
                    break;
                case 5:
                    deleteAddress();
                    break;
                case 0:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Unknown command");
            }
        }
    }

    private void deleteAddress() throws IOException, SftpException {
        System.out.println("Enter ip or domain for delete:");
        String value = scanner.nextLine();
        ValidationResult result = addressService.deleteAddress(value);
        if (!result.isValid()) {
            System.out.println("Cannot delete address: " + result.getMessage());
            return;
        }
        System.out.println(result.getMessage());
    }

    private void addAddress() throws IOException, SftpException {
        System.out.println("Enter domain: ");
        String domain = scanner.nextLine();

        System.out.println("Enter ip: ");
        String ip = scanner.nextLine();

        ValidationResult result = addressService.addAddress(domain, ip);
        // работает с результатом валидации
        if (result.isValid()) {
            System.out.println(result.getMessage());
        } else  {
            System.out.println("Cannot add address:");
            System.out.println(result.getMessage());
        }
    }

    private void showDomainByIp() throws SftpException, IOException {
        System.out.println("Enter IP: ");
        String ip = scanner.nextLine();
        String domain = addressService.findDomainByIp(ip);
        if (domain != null) {
            System.out.println("Domain: " + domain);
        } else {
            System.out.println("IP not found");
        }
    }

    /// Возвращаем IP через поиск по домену
    private void showIpByDomain() throws SftpException, IOException{
        System.out.println("Enter domain: ");
        String domain = scanner.nextLine();
        String ip = addressService.findIpByDomain(domain);
        if (ip != null) {
            System.out.println("IP: " + ip);
        } else {
            System.out.println("Domain not found");
        }
    }

    private void showAddresses() throws SftpException, IOException {
        List<Address> addresses = addressService.getAddresses();

        // выводим в консоль значения адресов
        addresses.forEach(a-> {
            System.out.println(a.getDomain() + ": " + a.getIp());
        });
    }


    /// Читает команду пользователя и преобразует её в число
    private int readCommand() {
        while (true) {
            String command = scanner.nextLine();
            try {
                return Integer.parseInt(command);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number");
                System.out.println("Select: ");
            }
        }
    }

    /// Вывод меню
    private void showMenu() {
        System.out.println();
        System.out.println("<< SFTP CLIENT >>");
        System.out.println("1. Show address list");
        System.out.println("2. Find IP by domain");
        System.out.println("3. Find domain by IP");
        System.out.println("4. Add address");
        System.out.println("5. Delete address");
        System.out.println("0. Exit");
        System.out.println("Select: ");
    }

}
