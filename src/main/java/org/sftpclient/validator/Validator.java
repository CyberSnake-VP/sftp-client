package org.sftpclient.validator;

import org.sftpclient.model.ValidationResult;

public class Validator {

    private static final int MAX_PORT = 65535;
    private static final int MIN_PORT = 1;

    public static ValidationResult validate(String host, int port, String login, String password) {
        if (!isValidHost(host)) {
            return new ValidationResult(false, "Host cannot be empty");
        }
        if (!isValidPort(port)) {
            return new ValidationResult(false, "Port must be between 1 and 65535");
        }
        if (!isValidLogin(login)) {
            return new ValidationResult(false, "Login cannot be empty");
        }
        if (!isValidPassword(password)) {
            return new ValidationResult(false, "Password cannot be empty");
        }

        return new ValidationResult(true, "");
    }

    private static boolean isValidPort(int port) {
        return port >= MIN_PORT && port <= MAX_PORT;
    }

    private static boolean isValidLogin(String login) {
        return login != null && !login.trim().isEmpty();
    }

    private static boolean isValidHost(String host) {
        return host != null && !host.trim().isEmpty();
    }
    private static boolean isValidPassword(String password) {
        return password != null && !password.trim().isEmpty();
    }

}
