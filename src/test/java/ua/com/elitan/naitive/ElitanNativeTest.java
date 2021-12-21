package ua.com.elitan.naitive;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import ua.com.elitan.util.ElitanUtils;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Testing of elitan.com.ua mobile native-application.
 * <p>
 * IMPORTANT:
 * Before test you need run appium server with command-line:
 * appium
 * <p>
 *  To stop appium server use Ctrl+C keyboard combination.
 */
class ElitanNativeTest {

    protected static AndroidDriver<WebElement> driver;

    protected static String appName = "ElitanMobileNative.apk";

    /*Please set true for real device and false for emulator testing*/
    private static final boolean isRealDevice = true;

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
        ElitanUtils.getAppFile(appName);

        DesiredCapabilities capabilities = ElitanUtils.getCommonDesiredCapabilities(isRealDevice);

        capabilities.setCapability("app", ElitanUtils.getAppFile(appName).getAbsolutePath());

        driver = new AndroidDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    /**
     * Tests login for registered user.
     */
    @Test
    void testLogin() {
        /*Registered user login and password*/
        String email = "usertest@gmail.com";
        String password = "testtest";

        driver.findElementById("ua.com.elitan.elitanmobilenative:id/emailText").sendKeys(email);
        driver.findElementById("ua.com.elitan.elitanmobilenative:id/passwordText").sendKeys(password);
        driver.findElementById("ua.com.elitan.elitanmobilenative:id/loginButton").click();

        String expected = "Добро пожаловать";
        MobileElement messageElement = (MobileElement) driver
                .findElementById("ua.com.elitan.elitanmobilenative:id/messageText");
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
