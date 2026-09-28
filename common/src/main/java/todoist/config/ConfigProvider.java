package todoist.config;

import org.aeonbits.owner.ConfigFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public final class ConfigProvider {

    private static final String COMMON_RESOURCE = "config/common.properties";
    private static final String LOCAL_RESOURCE = "config/local.properties";

    public static final ProjectConfig CONFIG = load();
    private static final Object REFRESH_LOCK = new Object();

    private ConfigProvider() {
    }

    private static ProjectConfig load() {
        Properties props = new Properties();
        loadResource(props, COMMON_RESOURCE, true);
        loadResource(props, LOCAL_RESOURCE, false);
        props.putAll(System.getProperties());
        return ConfigFactory.create(ProjectConfig.class, props);
    }

    private static void loadResource(Properties target, String resource, boolean required) {
        try (InputStream is = ConfigProvider.class.getClassLoader().getResourceAsStream(resource)) {
            if (is == null) {
                if (required) {
                    throw new IllegalStateException("Не найден обязательный файл конфигурации: " + resource);
                }
                return;
            }
            target.load(new InputStreamReader(is, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить " + resource, e);
        }
    }

    public static void refresh() {
        synchronized (REFRESH_LOCK) {
            CONFIG.reload();
        }
    }
}