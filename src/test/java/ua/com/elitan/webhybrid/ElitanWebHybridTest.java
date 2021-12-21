package ua.com.elitan.webhybrid;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import ua.com.elitan.util.ElitanUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
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
class ElitanWebHybridTest {

    protected static AndroidDriver<WebElement> driver;

    protected static String appName = "ElitanMobileHybrid.apk";

    /*Please set true for web app and false for hybrid app testing*/
    private static final boolean isWebApp = false;
    /*Please set true for real device and false for emulator testing*/
    private static final boolean isRealDevice = true;

    /**
     * Create appium driver with settled desire capabilities.
     * <p>
     * IMPORTANT:
     * You must correct desired capabilities for your case.
     *
     * @throws MalformedURLException if wrong appium server URL
     */
    @BeforeAll
    public static void setUpClass() throws MalformedURLException {

        DesiredCapabilities capabilities = ElitanUtils.getCommonDesiredCapabilities(isRealDevice);

        if (isWebApp) {
            capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "Chrome");
        } else {
            capabilities.setCapability("app", ElitanUtils.getAppFile(appName).getAbsolutePath());
            capabilities.setCapability("autoWebview", true);
        }

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
    void testLogin() {
        /*Registered user login and password*/
        String email = "usertest@gmail.com";
        String password = "testtest";
        String username = "Testirovshichik";

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
     * Tests login for non-registered user.
     */
    @Test
    void testInvalidLogin() {
        /*Registered user login and password*/
        loginUser("thereissome@textgmail.com", "qwerty");

        String expected = "ERROR";
        WebElement messageElement = driver.findElement(
                By.xpath("//*[@id=\"post-7\"]/div/div/div/div/div/div[1]/ul/li/strong"));
        Assertions.assertEquals(expected, messageElement.getText());
    }

    private void loginUser(String email, String password) {
        String username = email.substring(0, email.indexOf('@'));

        driver.get("https://elitan.com.ua/my-account/");
        driver.findElement(By.xpath("//input[@id='username']")).sendKeys(email);
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys(password);
        driver.findElement(By.cssSelector("#rememberme")).click();
        driver.findElement(By.cssSelector(".woocommerce-form-login__submit")).click();
    }

    /**
     * Tests registration with valid data
     */
    @Test
    void testValidRegistration() {
        String name = "TestQA";
        String surname = "QATest";
        String phone = "0665711464";
        String email = "flydiecry+211103@gmail.com";
        String password = "Test1234!";

        driver.get("https://elitan.com.ua/my-account/");
        driver.findElement(By.xpath("//*[@id=\"reg_billing_first_name\"]")).sendKeys(name);
        driver.findElement(By.xpath("//*[@id=\"reg_billing_last_name\"]")).sendKeys(surname);
        driver.findElement(By.xpath("//*[@id=\"reg_billing_phone\"]")).sendKeys(phone);
        driver.findElement(By.xpath("//*[@id=\"reg_email\"]")).sendKeys(email);
        driver.findElement(By.xpath("//*[@id=\"reg_password\"]")).sendKeys(password);
        driver.findElement(By.xpath("//*[@id=\"customer_login\"]/div[2]/form/p[6]/button")).click();

       /*Для успешной регистрации*/
//        String expected = "Выйти";
//        WebElement messageElement = driver.findElement(
//                By.cssSelector("#post-7 > div > div > div > div > div > div > p:nth-child(2) > a"));
//        String messageText = messageElement.getText();

        /*Для регистрации уже зарегистрированного пользователя*/
        String expected = "Ошибка:";
        WebElement messageElement = driver.findElement(
                By.xpath("//*[@id=\"post-7\"]/div/div/div/div/div/div[1]/ul/li"));
        String[] messageWords = messageElement.getText().split("\\s");
        String messageText = messageWords[0];

        Assertions.assertEquals(expected, messageText);
    }

    /**
     * Tests registration with Empty Fields
     */
    @Test
    void testInvalidRegistrationEmptyFields() {
        driver.get("https://elitan.com.ua/my-account/");
        driver.findElement(By.xpath("//*[@id=\"customer_login\"]/div[2]/form/p[6]/button")).click();

        String expected = "Ошибка:";
        WebElement messageElement = driver.findElement(
                By.xpath("//*[@id=\"post-7\"]/div/div/div/div/div/div[1]/ul/li"));
        String[] messageWords = messageElement.getText().split("\\s");
        String messageText = messageWords[0];

        Assertions.assertEquals(expected, messageText);
    }

    /**
     * Tests changing contact info
     */
    @Test
    void changeContactInfo() {
        loginUser("usertest@gmail.com", "testtest");

        driver.findElement(By.cssSelector(
                "#post-7 > div > div > div > div > div > nav > ul > li.woocommerce-MyAccount-navigation-link.woocommerce-MyAccount-navigation-link--edit-account > a")).click();

        String name = "Tessst";
        String surname = "QATesterkok";
        String dispname = "Testirovshichik";

        driver.findElement(By.xpath("//*[@id=\"account_first_name\"]")).clear();
        driver.findElement(By.xpath("//*[@id=\"account_first_name\"]")).sendKeys(name);
        driver.findElement(By.xpath("//*[@id=\"account_last_name\"]")).clear();
        driver.findElement(By.xpath("//*[@id=\"account_last_name\"]")).sendKeys(surname);
        driver.findElement(By.xpath("//*[@id=\"account_display_name\"]")).clear();
        driver.findElement(By.xpath("//*[@id=\"account_display_name\"]")).sendKeys(dispname);
        driver.findElement(By.cssSelector("#post-7 > div > div > div > div > div > div > form > p:nth-child(9) > button")).click(); //Вот эту кнопку не может определить
        String expected = "Профиль успешно изменён.";
        WebElement messageElement = driver.findElement(By.cssSelector("#post-7 > div > div > div > div > div > div > div > div"));
        Assertions.assertEquals(expected, messageElement.getText());
    }

    /**
     * Tests adding item to the cart
     */
    @Test
    void addItemToTheCart() {
        WebElement addToCartButton = driver.findElement(By
                .xpath("//*[@id=\"post-24\"]/div/div/div/div/div[8]/div/div/div/div/ul/li[1]/a[2]"));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", addToCartButton);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebElement goToCartButton = driver.findElement(By
                .xpath("//*[@id=\"masthead\"]/div[2]/div/div[3]/div[3]/div[1]/a"));
        js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", goToCartButton);

        String expected = "Портмоне Pro-Covers ПКМ-40, черный";
        WebElement messageElement = driver.findElement(By
                .xpath("//*[@id=\"post-5\"]/div/div/div/div/div/form/table/tbody/tr[1]/td[3]/a"));
        String messageText = messageElement.getText();
        Assertions.assertEquals(expected, messageText);
    }

    /**
     * Tests removing item from the cart
     */
    @Test
    void removeItemFromTheCart() {

        driver.get("https://elitan.com.ua/cart/");

        String expected = "Ваша корзина пока пуста.";
        WebElement messageElement = driver.findElement(
                By.xpath("//*[@id=\"post-5\"]/div/div/div/div/div/p[1]"));
        String messageText = messageElement.getText();
        Assertions.assertEquals(expected, messageText);
    }

    /**
     * Appium driver closing.
     */
    @AfterAll
    public static void tearDown() {
        driver.quit();
    }
}
