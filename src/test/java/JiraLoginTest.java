import org.openqa.selenium.WebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JiraLoginTest {
    private static WebDriver driver;
    private static final String BASE_URL = "https://jira-auto.codecool.metastage.net/secure/Dashboard.jspa";
    private static final String PATH_TO_WEBDRIVER = "/usr/bin/chromedriver";
    private static String username;
    private static String password;

    final static String WRONG_PASSWORD = "x";
    final static String CORRECT_USERNAME = "automation62";
    final static String EMPTY_PASSWORD = "";
    final static String EMPTY_USERNAME = "";



    private static final Logger logger = LogManager.getLogger(JiraLoginTest.class);

    @BeforeAll
    public static void setUp(){

        if (driver == null) {
            System.setProperty("webdriver.chrome.driver", PATH_TO_WEBDRIVER);
            driver = new ChromeDriver();
            driver.manage().window().maximize();
        }
        driver.get(BASE_URL);
    }

    @Test
    public void testPositiveTestcase() throws Exception {
        // the correct password is in the config.properties file
        correctLoginTestUser();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//        WebElement welcomeMessageElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("gadget-0-title")));
//        WebElement welcomeMessageElement = driver.findElement(By.id("gadget-0-title"));
        WebElement welcomeMessageElement = driver.findElement(By.className("aui-avatar-inner"));
        assertTrue(welcomeMessageElement.isDisplayed());
        System.out.println(welcomeMessageElement.isDisplayed());
        tearDown();
    }
    @Test
    public void testWrongPassword() throws Exception {
        // login with wrong password
        loginTestUser(CORRECT_USERNAME, WRONG_PASSWORD);
        Thread.sleep(10000);
        try {
            WebElement welcomeMessageElement = driver.findElement(By.className("aui-avatar-inner"));
            assertFalse(welcomeMessageElement.isDisplayed());
            System.out.println("Is Login successful? : " + welcomeMessageElement.isDisplayed());
        } catch (Exception e) {
            logger.error("Login Failed, wrong password: " + e.getMessage());
        }
    }


    @AfterAll
    public static void tearDown(){
        if (driver != null) {
            driver.quit();
        }
    }

    private static void getCredentials() {
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream("src/test/resources/config.properties")) {
            prop.load(input);
        } catch (Exception e) {
            logger.error("Failed to load user credentials: " + e.getMessage());
        }

        username = prop.getProperty("username");
        password = prop.getProperty("password");
    }


    private static void loginTestUser(String username, String password) throws Exception{

        try {
            driver.findElement(By.id("login-form-username")).sendKeys(username);
            driver.findElement(By.id("login-form-password")).sendKeys(password);
            driver.findElement(By.id("login")).click();

        } catch (Exception e) {
            logger.error("Login failed: " + e.getMessage());
            throw new Exception("Login failed: " + e.getMessage());
        }
    }

    private static void correctLoginTestUser() throws Exception{

        try {
            getCredentials();
            driver.findElement(By.id("login-form-username")).sendKeys(username);
            driver.findElement(By.id("login-form-password")).sendKeys(password);
            driver.findElement(By.id("login")).click();

        } catch (Exception e) {
            logger.error("Login failed: " + e.getMessage());
            throw new Exception("Login failed: " + e.getMessage());
        }
    }
}
