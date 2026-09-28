package todoist.web.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import org.openqa.selenium.TimeoutException;
import todoist.web.data.ErrorMessage;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class LoginPage extends BasePage {

    private static final String LOGIN_FORM_CONTEXT = "//form[.//input[@type='email']]";
    private static final String INLINE_ERROR_XPATH = LOGIN_FORM_CONTEXT + "//*[contains(text(), '%s')]";
    private static final String GLOBAL_FORM_ERROR_XPATH = LOGIN_FORM_CONTEXT + "//*[text()='%s']";
    private static final String CAPTCHA_XPATH = "//*[contains(text(), '%s')]";

    private final SelenideElement emailField = $("form input[autocomplete='email']");
    private final SelenideElement passwordField = $("form input[autocomplete='current-password']");
    private final SelenideElement submitButton = $("form button[type='submit']");

    private SelenideElement getInlineErrorElement(String text) {
        return $x(String.format(INLINE_ERROR_XPATH, text));
    }

    private SelenideElement getGlobalFormErrorElement(String text) {
        return $x(String.format(GLOBAL_FORM_ERROR_XPATH, text));
    }

    private SelenideElement getCaptchaMessage() {
        return $x(String.format(CAPTCHA_XPATH, ErrorMessage.CAPTCHA_FAILED.getText()));
    }

    @Step("Открыть страницу авторизации")
    public LoginPage open() {
        openPage("/auth/login");
        return this;
    }

    @Step("Ввести email: {email}")
    public LoginPage enterEmail(String email) {
        emailField.shouldBe(visible).setValue(email);
        return this;
    }

    @Step("Ввести пароль")
    public LoginPage enterPassword(String password) {
        passwordField.shouldBe(visible).setValue(password);
        return this;
    }

    @Step("Нажать кнопку 'Войти'")
    public LoginPage clickSubmit() {
        submitButton.shouldBe(Condition.interactable).click();
        return this;
    }

    @Step("Авторизоваться пользователем: {email}")
    public LoginPage login(String email, String password) {
        return this.enterEmail(email)
                .enterPassword(password)
                .clickSubmit();
    }

    @Step("Проверить инлайн-ошибку валидации пароля: {error}")
    public LoginPage verifyPasswordInlineError(ErrorMessage error) {
        passwordField.shouldHave(Condition.attribute("aria-invalid", "true"));
        getInlineErrorElement(error.getText()).shouldBe(visible);
        return this;
    }

    @Step("Проверить глобальную ошибку формы: {error}")
    public LoginPage verifyFormError(ErrorMessage error) {
        getGlobalFormErrorElement(error.getText()).shouldBe(visible);
        return this;
    }

    @Step("Проверить, не заблокирован ли вход капчей")
    public boolean isCaptchaBlocked() {
        try {
            Selenide.Wait().withTimeout(Duration.ofSeconds(10)).until(d ->
                    getCaptchaMessage().exists()
                            || !WebDriverRunner.url().contains("/auth/login")
                            || passwordField.has(Condition.attribute("aria-invalid", "true"))
                            || getGlobalFormErrorElement(ErrorMessage.INVALID_CREDENTIALS.getText()).exists()
                            || getGlobalFormErrorElement(ErrorMessage.EMAIL_REQUIRED.getText()).exists());
        } catch (TimeoutException ignored) {
        }
        return getCaptchaMessage().exists();
    }
}