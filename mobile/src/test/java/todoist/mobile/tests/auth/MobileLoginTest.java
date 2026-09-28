package todoist.mobile.tests.auth;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import todoist.config.ConfigProvider;
import todoist.mobile.screens.MainScreen;
import todoist.mobile.screens.WelcomeScreen;
import todoist.mobile.tests.base.BaseAuthTest;

@Epic("Авторизация и аккаунт")
@Feature("Mobile: Авторизация и регистрация")
@Tag("androidLocal")
public class MobileLoginTest extends BaseAuthTest {

    private final Faker faker = new Faker();

    @BeforeEach
    void prepareCleanWelcomeScreen() {
        System.out.println("[Precondition] Выполнение безопасного нативного сброса приложения Todoist...");
        WelcomeScreen welcomeScreen = new WelcomeScreen(driver());
        welcomeScreen.clearAppSession();
    }

    @Test
    @Story("Успешный вход")
    @DisplayName("Успешный вход в приложение по Email")
    void successLoginTest() {
        WelcomeScreen welcomeScreen = new WelcomeScreen(driver());

        MainScreen mainScreen = welcomeScreen.openEmailMenu()
                .selectLoginFromDropdown()
                .fillCredentials(
                        ConfigProvider.CONFIG.testEmail(),
                        ConfigProvider.CONFIG.testPassword()
                )
                .confirmLogin();

        Assertions.assertTrue(
                mainScreen.isInboxPageDisplayed(),
                "Главная страница 'Входящие' не отобразилась после авторизации!"
        );
    }

    @Test
    @Story("Негативные сценарии регистрации")
    @DisplayName("Неуспешная регистрация: невалидный Email")
    void registrationWithInvalidEmailTest() {
        WelcomeScreen welcomeScreen = new WelcomeScreen(driver());

        String invalidEmail = faker.lorem().word() + "Email.com";
        String randomPassword = faker.internet().password(8, 12);

        System.out.println("[Faker] Сгенерирован невалидный email: " + invalidEmail);

        welcomeScreen.openEmailMenu()
                .selectRegisterFromDropdown()
                .fillCredentials(invalidEmail, randomPassword);

        welcomeScreen.confirmLogin();

        boolean isStillOnWelcomeScreen = welcomeScreen.isDisplayed(
                io.appium.java_client.AppiumBy.androidUIAutomator(
                        "new UiSelector().resourceId(\"email\")"
                )
        );

        Assertions.assertTrue(
                isStillOnWelcomeScreen,
                "Приложение должно было заблокировать регистрацию и оставить пользователя на форме ввода!"
        );
    }

    @Test
    @Story("Негативные сценарии регистрации")
    @DisplayName("Неуспешная регистрация: короткий пароль")
    void registrationWithShortPasswordTest() {
        WelcomeScreen welcomeScreen = new WelcomeScreen(driver());

        String validEmail = faker.internet().emailAddress();
        String shortPassword = faker.internet().password(1, 3);

        System.out.println("[Faker] Сгенерирован короткий пароль: " + shortPassword);

        welcomeScreen.openEmailMenu()
                .selectRegisterFromDropdown()
                .fillCredentials(validEmail, shortPassword);

        welcomeScreen.confirmLogin();

        boolean isStillOnWelcomeScreen = welcomeScreen.isDisplayed(
                io.appium.java_client.AppiumBy.androidUIAutomator(
                        "new UiSelector().resourceId(\"password\")"
                )
        );

        Assertions.assertTrue(
                isStillOnWelcomeScreen,
                "Приложение должно блокировать переход при слишком коротком пароле!"
        );
    }

    @Test
    @Story("Негативные сценарии регистрации")
    @DisplayName("Неуспешная регистрация: пустые обязательные поля")
    void registrationWithEmptyFieldsTest() {
        WelcomeScreen welcomeScreen = new WelcomeScreen(driver());

        welcomeScreen.openEmailMenu()
                .selectRegisterFromDropdown();

        welcomeScreen.attemptConfirmWithoutValidation();

        boolean isStillOnRegistration = welcomeScreen.isRegisterTitleDisplayed();
        Assertions.assertTrue(
                isStillOnRegistration,
                "Приложение должно было проигнорировать пустой клик и оставить пользователя на экране 'Создать аккаунт'!"
        );
    }
}