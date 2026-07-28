package org.sftpclient.input;

import org.sftpclient.model.ConnectionInfo;
import org.sftpclient.model.ValidationResult;
import org.sftpclient.validator.Validator;

import java.util.Scanner;

// Получение пользовательских данных
public class ConnectionInfoReader {
    private final Scanner scanner;

    public ConnectionInfoReader(Scanner scanner) {
        this.scanner = scanner;
    }

    // Метод возвращает объект передачи данных для нашего подключения
    public ConnectionInfo read() {

        while (true) {
            String host = readHost();
            String portText = readPort();

            int port;
            try {
                port = Integer.parseInt(portText); // валидируем через catch
            } catch (NumberFormatException e) {
                System.out.println("Port must be an integer");
                System.out.println("Please try again.");

                continue;
            }

            String login = readLogin();
            String password = readPassword();

            ValidationResult result = Validator.validate(host, port, login, password);

            if (result.isValid()) {
                return new ConnectionInfo(host, port, login, password);
            } else {
                System.out.println(result.getMessage());
                System.out.println("Please try again");
            }
        }
    }

    private String readHost() {
        System.out.println("Host: ");
        String host = scanner.nextLine();
        return host;
    }
    private String readPort() {
        System.out.println("Port: ");
        String portText = scanner.nextLine();
        return portText;
    }
    private String readLogin() {
        System.out.println("Login: ");
        String login = scanner.nextLine();
        return login;
    }
    private String readPassword() {
        System.out.println("Password: ");
        String password = scanner.nextLine();
        return password;
    }
}
