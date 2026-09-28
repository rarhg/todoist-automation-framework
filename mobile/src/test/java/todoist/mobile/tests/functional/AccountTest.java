package todoist.mobile.tests.functional;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import todoist.mobile.screens.MainScreen;
import todoist.mobile.screens.WelcomeScreen;
import todoist.mobile.tests.base.BaseAccountTest;

@Epic("Авторизация и аккаунт")
@Feature("Mobile: Управление аккаунтом")
@Tag("androidLocal")
@Story("Выход из аккаунта")
public class AccountTest extends BaseAccountTest {

    @Test
    @DisplayName("Успешный выход из аккаунта (Log Out)")
    void successLogOutTest() {
        MainScreen mainScreen = new MainScreen(driver());

        WelcomeScreen welcomeScreen = mainScreen.openBrowseTab()
                .openSettings()
                .logOut();

        boolean isWelcomeScreenDisplayed = welcomeScreen.isWelcomeScreenVisible();

        Assertions.assertTrue(
                isWelcomeScreenDisplayed,
                "После логаута пользователь не вернулся на стартовый приветственный экран приложения!"
        );
    }
}