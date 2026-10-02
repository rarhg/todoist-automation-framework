package todoist.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Reloadable;

@Config.Sources({
        "system:properties",
        "system:env",
        "classpath:config/local.properties",
        "classpath:config/common.properties"
})
public interface ProjectConfig extends Config, Reloadable {

    @Key("base.url")
    @DefaultValue("https://todoist.com")
    String baseUrl();

    @Key("api.url")
    @DefaultValue("https://api.todoist.com")
    String apiUrl();

    @Key("api.token")
    String apiToken();

    @Key("api.version")
    @DefaultValue("/api/v1")
    String apiVersion();

    @Key("test.email")
    String testEmail();

    @Key("test.password")
    String testPassword();

    @Key("browser")
    @DefaultValue("CHROME")
    String browser();

    @Key("browser.version")
    String browserVersion();

    @Key("browser.size")
    @DefaultValue("1920x1080")
    String browserSize();

    @Key("is.remote")
    @DefaultValue("false")
    boolean isRemote();

    @Key("remote.url")
    String remoteUrl();

    @Key("mobile.provider")
    @DefaultValue("browserstack")
    String mobileProvider();

    @Key("bs.username")
    String browserstackUsername();

    @Key("bs.access.key")
    String browserstackAccessKey();

    @Key("bs.app.url")
    String browserstackAppUrl();

    @Key("bs.device")
    @DefaultValue("Google Pixel 7")
    String browserstackDevice();

    @Key("bs.os.version")
    @DefaultValue("13.0")
    String browserstackOsVersion();

    @Key("local.apk.path")
    String localApkPath();

    @Key("local.device.name")
    @DefaultValue("emulator-5554")
    String localDeviceName();

    @Key("app.package")
    String appPackage();

    @Key("app.activity")
    String appActivity();

    @Key("android.sdk.home")
    String androidSdkHome();

    @Key("device.timezone")
    @DefaultValue("Europe/Moscow")
    String deviceTimeZone();

    @Key("bs.timezone")
    @DefaultValue("Moscow")
    String browserstackTimezone();

    @Key("bs.project.name")
    @DefaultValue("Todoist Mobile Automation")
    String browserstackProjectName();

    @Key("bs.build.name")
    @DefaultValue("Diploma_Mobile_Build")
    String browserstackBuildName();

    @Key("mock.test.token")
    @DefaultValue("test_local_token_secure_123")
    String mockTestToken();

    @Key("mock.invalid.token")
    @DefaultValue("invalid_token_xyz_999")
    String mockInvalidToken();

    @Key("web.timeout.ms")
    @DefaultValue("15000")
    long webTimeoutMs();

    @Key("web.headless")
    @DefaultValue("false")
    boolean isHeadless();

    @Key("web.page.load.strategy")
    @DefaultValue("eager")
    String pageLoadStrategy();
}