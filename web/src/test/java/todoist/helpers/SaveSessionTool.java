package todoist.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import todoist.config.ConfigProvider;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SaveSessionTool {

    private static final Logger log = LoggerFactory.getLogger(SaveSessionTool.class);

    public static void main(String[] args) throws Exception {
        String debugAddress = System.getProperty("debug.address", "localhost:9222");
        Path target = Path.of(System.getProperty(CookieAuthManager.STATE_FILE_PROPERTY,
                CookieAuthManager.STATE_FILE_DEFAULT));

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", debugAddress);
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(ConfigProvider.CONFIG.baseUrl() + "/app/inbox");
            Thread.sleep(3000);

            String url = driver.getCurrentUrl();
            if (url.contains("/auth/")) {
                throw new IllegalStateException("Браузер не залогинен (страница " + url
                        + "). Войдите в Todoist вручную в этом окне и повторите.");
            }

            Object identity = ((JavascriptExecutor) driver)
                    .executeScript("return localStorage.getItem('auth_identity');");
            if (identity == null) {
                throw new IllegalStateException("В localStorage нет auth_identity. Дождитесь полной загрузки "
                        + "приложения в окне и повторите.");
            }

            List<Map<String, Object>> cookies = new ArrayList<>();
            boolean hasSessionCookie = false;
            for (Cookie c : driver.manage().getCookies()) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("name", c.getName());
                m.put("value", c.getValue());
                m.put("domain", c.getDomain());
                m.put("path", c.getPath());
                m.put("expiry", c.getExpiry() == null ? null : c.getExpiry().getTime());
                m.put("secure", c.isSecure());
                m.put("httpOnly", c.isHttpOnly());
                m.put("sameSite", c.getSameSite());
                cookies.add(m);
                if (c.getName().equals("tduser") || c.getName().equals("todoistd")) {
                    hasSessionCookie = true;
                }
            }
            if (!hasSessionCookie) {
                throw new IllegalStateException("Не найдены куки tduser/todoistd. Вход не завершён.");
            }

            Map<String, Object> root = new LinkedHashMap<>();
            root.put("savedAt", Instant.now().toString());
            root.put("cookies", cookies);
            root.put("localStorage", Map.of("auth_identity", identity.toString()));

            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(target.toFile(), root);
            log.info("Сессия сохранена: {} (кук: {})", target.toAbsolutePath(), cookies.size());
        } finally {
            driver.quit();
        }
    }
}
