package ua.com.elitan.web;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Testing of elitan.com.ua mobile web-application.
 * <p>
 * IMPORTANT:
 * Before test you need run appium server with command-line:
 * appium --chromedriver-executable /path/to/my/chromedriver
 * <p>
 * The chromedriver version have to match mobile Chrome application
 * in the emulator or real device.
 * <p>
 * To stop appium server use Ctrl+C keyboard combination.
 */
public class ElitanWebTest {

    protected static AndroidDriver<WebElement> driver;

    /*Please set true for real device and false for emulator testing*/
    private static final boolean isRealDevice = false;

    /**
     * Create appium driver with settled desire capabilities.
     * <p>
     * IMPORTANT:
     * You must correct desired capabilities for your case
     *
     * @throws MalformedURLException if wrong appium server URL
     */
    @BeforeAll
    public static void setUpClass() throws MalformedURLException {

        DesiredCapabilities capabilities = new DesiredCapabilities();
        if (isRealDevice) {
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "device");
            capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "9.0");
        } else {
            capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
            capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "5.0");
        }
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME,
                "UiAutomator2");
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Google Nexus 5");
        capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "Chrome");
        driver = new AndroidDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
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

        driver.get("https://elitan.com.ua/my-account/");
        driver.findElement(By.xpath("//input[@id='username']")).sendKeys(email);
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys(password);
        driver.findElement(By.cssSelector("#rememberme")).click();
        driver.findElement(By.cssSelector(".woocommerce-form-login__submit")).click();

        String expected = "Добро пожаловать, " + username + " (не " + username + "? Выйти)";
        WebElement messageElement = driver.findElement(
                By.cssSelector(".woocommerce-MyAccount-content > p:nth-child(2)"));
        Assertions.assertEquals(expected, messageElement.getText());
    }
}
