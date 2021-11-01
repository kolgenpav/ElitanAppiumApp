package ua.com.elitan.hybrid;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Testing of elitan.com.ua mobile hybrid-application.
 * <p>
 * IMPORTANT:
 * Before test you need run appium server with command-line:
 * appium --chromedriver-executable /path/to/my/chromedriver
 * <p>
 *  The chromedriver version have to match System Web View application
 *  in the emulator or real device.
 *  <p>
 *  To stop appium server use Ctrl+C keyboard combination.
 */
public class ElitanHybridTest {

    protected static AndroidDriver<WebElement> driver;

    protected static String appName = "ElitanMobileHybrid.apk";

    /*Please set true for real device and false for emulator testing*/
    private static final boolean isRealDevice = false;

    /**
     * Create appium driver with settled desire capabilities.
     * <p>
     * IMPORTANT:
     * You must correct desired capabilities for your case.
     *
     * @throws IOException if path to apk being tested is invalid
     */
    @BeforeAll
    public static void setUpClass() throws IOException {

        File classpathRoot = new File(System.getProperty("user.dir"));
        File appDir = new File(classpathRoot, "apps");
        File app = new File(appDir.getCanonicalPath(), appName);

        DesiredCapabilities capabilities = new DesiredCapabilities();
        if (isRealDevice) {
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "device");
            capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "9.0");
        } else {
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android10Phone");
            capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "10.0");
        }
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME,
                "UiAutomator2");
        capabilities.setCapability("app", app.getAbsolutePath());
        capabilities.setCapability("autoWebview", true);

        driver = new AndroidDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        /*Print contexts*/
        Set<String> contextNames = driver.getContextHandles();
        for (String contextName : contextNames) {
            System.out.println(contextName); //prints out something like NATIVE_APP \n WEBVIEW_1
        }
    }

    /**
     * Tests login for registered user.
     */
    @Test
    public void testLogin() {
        /*Registered user login and password*/
        String email = "usertest@gmail.com";
        String password = "testtest";
        String username = email.substring(0, email.indexOf('@'));

        driver.get("https://elitan.com.ua/");
        driver.findElement(By.linkText("Вход/ Регистрация")).click();
        /*Opens authorization page /my-account*/
        driver.findElement(By.cssSelector("#username")).sendKeys(email);
        driver.findElement(By.cssSelector("#password")).sendKeys(password);
        driver.findElement(By.cssSelector(".woocommerce-form-login__submit")).click();

        String expected = "Добро пожаловать, " + username + " (не " + username + "? Выйти)";
        WebElement messageElement = driver.findElement(
                By.cssSelector(".woocommerce-MyAccount-content > p:nth-child(2)"));
        Assertions.assertEquals(expected, messageElement.getText());
    }

    /**
     * Appium driver closing.
     */
    @AfterAll
    public static void tearDown() {
        driver.quit();
    }
}
