package todoist.web.tests;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import todoist.web.data.ErrorMessage;
import todoist.web.pages.InboxPage;
import todoist.web.pages.LoginPage;

import static todoist.config.ConfigProvider.CONFIG;

@Epic("Авторизация и аккаунт")
@Feature("Web: Авторизация")
@Tag("web")
@Disabled("Форма входа Todoist защищена капчей и отклоняет автоматизированный браузер; "
        + "сценарии входа проверяются вручную (см. ручные тест-кейсы)")
@DisplayName("Web: Тесты на авторизацию")
public class LoginTest extends BaseWebTest {

    private final LoginPage loginPage = new LoginPage();
    private final InboxPage inboxPage = new InboxPage();

    private void skipIfCaptchaBlocked() {
        Assumptions.assumeFalse(loginPage.isCaptchaBlocked(),
                "Todoist показал капчу автоматизированному браузеру — сценарий проверяется вручную");
    }

    @Test
    @Story("Успешный вход")
    @DisplayName("Успешный логин через UI форму ввода")
    void shouldLoginSuccessfully() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();

        loginPage.open()
                .login(CONFIG.testEmail(), CONFIG.testPassword());
        skipIfCaptchaBlocked();

        inboxPage.verifyPageOpened();
    }

    @Test
    @Story("Негативные сценарии")
    @DisplayName("Неуспешный логин: Неверные почта и пароль")
    void shouldNotLoginWithInvalidCredentials() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();

        Faker faker = new Faker();
        String randomEmail = faker.internet().emailAddress();
        String randomPassword = faker.internet().password(8, 16);

        loginPage.open()
                .login(randomEmail, randomPassword);
        skipIfCaptchaBlocked();

        loginPage.verifyFormError(ErrorMessage.INVALID_CREDENTIALS);
    }

    @Test
    @Story("Негативные сценарии")
    @DisplayName("Неуспешный логин: Пустые поля ввода")
    void shouldNotLoginWithEmptyFields() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();

        loginPage.open()
                .login("", "");
        skipIfCaptchaBlocked();

        loginPage.verifyPasswordInlineError(ErrorMessage.EMPTY_PASSWORD);
    }

    @Test
    @Story("Негативные сценарии")
    @DisplayName("Неуспешный логин: Введен только пароль")
    void shouldNotLoginWithoutEmail() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();

        loginPage.open()
                .login("", CONFIG.testPassword());
        skipIfCaptchaBlocked();

        loginPage.verifyFormError(ErrorMessage.EMAIL_REQUIRED);
    }
}