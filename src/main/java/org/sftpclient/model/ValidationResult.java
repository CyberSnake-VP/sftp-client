package org.sftpclient.model;

/// Вынесем результат валидации. Чтобы Validator не хранил состояние, для работы консоли.
public class ValidationResult {
    private final boolean valid;
    private final String message;

    public ValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }
}
