package todoist.helpers;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

public class CookieAuthManager {

    public static final String STATE_FILE_PROPERTY = "auth.state.file";
    public static final String STATE_FILE_DEFAULT = "auth-state.json";
    public static final String STATE_ENV = "AUTH_STATE_JSON";

    private static final List<String> SESSION_COOKIES = List.of("tduser", "todoistd");
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(CookieAuthManager.class);

    private static volatile JsonNode state;

    public static synchronized void ensureGlobalAuth() {
        if (state != null) {
            return;
        }
        JsonNode loaded = load();
        validate(loaded);
        state = loaded;
        log.info("[AUTH] Сессия загружена из сохранённого состояния ({} кук)", loaded.path("cookies").size());
    }

    public static void injectSession() {
        if (state == null) {
            throw new IllegalStateException("[CookieAuthManager] Попытка инжекции пустой сессии!");
        }

        for (JsonNode c : state.path("cookies")) {
            try {
                Cookie.Builder builder = new Cookie.Builder(c.path("name").asText(), c.path("value").asText())
                        .path(c.path("path").asText("/"))
                        .isSecure(c.path("secure").asBoolean(false))
                        .isHttpOnly(c.path("httpOnly").asBoolean(false));

                String domain = c.path("domain").asText("");
                if (!domain.isBlank()) {
                    builder.domain(domain);
                }
                long expiry = c.path("expiry").asLong(0);
                if (expiry > 0) {
                    builder.expiresOn(new Date(expiry));
                }
                String sameSite = c.path("sameSite").asText("");
                if (!sameSite.isBlank()) {
                    builder.sameSite(sameSite);
                }
                WebDriverRunner.getWebDriver().manage().addCookie(builder.build());
            } catch (Exception e) {
                log.error("[CookieAuthManager] Не удалось внедрить куку {}: {}", c.path("name").asText(), e.getMessage());
            }
        }

        String authIdentity = state.path("localStorage").path("auth_identity").asText("");
        if (!authIdentity.isBlank()) {
            Selenide.executeJavaScript("localStorage.setItem('auth_identity', arguments[0]);", authIdentity);
        }
    }

    private static JsonNode load() {
        try {
            String env = System.getenv(STATE_ENV);
            if (env != null && !env.isBlank()) {
                return MAPPER.readTree(env);
            }
            Path file = Path.of(System.getProperty(STATE_FILE_PROPERTY, STATE_FILE_DEFAULT));
            if (!Files.exists(file)) {
                throw new IllegalStateException("Файл сессии не найден: " + file.toAbsolutePath()
                        + ". Войдите в Todoist вручную и запустите ./gradlew saveSession (см. README).");
            }
            return MAPPER.readTree(Files.readString(file, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать файл сессии: " + e.getMessage(), e);
        }
    }

    private static void validate(JsonNode node) {
        long now = System.currentTimeMillis();
        boolean hasSessionCookie = false;

        for (JsonNode c : node.path("cookies")) {
            if (!SESSION_COOKIES.contains(c.path("name").asText())) {
                continue;
            }
            hasSessionCookie = true;
            long expiry = c.path("expiry").asLong(0);
            if (expiry > 0 && expiry < now) {
                throw new IllegalStateException("Сохранённая сессия устарела (кука " + c.path("name").asText()
                        + " истекла). Войдите в Todoist вручную и запустите ./gradlew saveSession.");
            }
        }
        if (!hasSessionCookie) {
            throw new IllegalStateException("В файле сессии нет кук авторизации (tduser/todoistd). "
                    + "Войдите в Todoist вручную и запустите ./gradlew saveSession.");
        }
        if (node.path("localStorage").path("auth_identity").asText("").isBlank()) {
            log.warn("[AUTH] В файле сессии нет auth_identity — вход может не сработать");
        }
    }
}