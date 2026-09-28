package todoist.web.pages;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.open;

public abstract class BasePage {

    @Step("Открыть страницу по относительному пути: {relativeUrl}")
    public void openPage(String relativeUrl) {
        open(relativeUrl);
    }
}
