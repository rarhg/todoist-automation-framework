package todoist.web.data;

public enum ErrorMessage {
    INVALID_CREDENTIALS("Неверный Email или пароль."),
    EMPTY_PASSWORD("В пароле должно быть не менее 8 символов."),
    EMAIL_REQUIRED("Пожалуйста, введите действующий Email-адрес."),
    CAPTCHA_FAILED("Проверка капчи не прошла");

    private final String text;

    ErrorMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}