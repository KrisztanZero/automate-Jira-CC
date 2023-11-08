import models.JiraIssueParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Stream;

public class CreateIssueTest {
    private static WebDriver driver;
    private static final Logger logger = LogManager.getLogger(CreateIssueTest.class);
    private static final String BASE_URL = "https://jira-auto.codecool.metastage.net/secure/Dashboard.jspa";
    private static String username;
    private static String password;

    static Stream<JiraIssueParameters> testdata(){
        return Stream.of(
                new JiraIssueParameters(
                        "TOUCAN project (TOUCAN)",
                        "Story",
                        "Story Type Issue Creation Test",
                        "src/test/resources/testdata/testUpload.txt",
                        "7/Nov/23",
                        "Test description",
                        "Auto Tester 61",
                        "Highest",
                        Arrays.asList("Test", "Label"),
                        "1h",
                        "1h"
                ),
                new JiraIssueParameters(
                        "TOUCAN project (TOUCAN)",
                        "Bug",
                        "Bug Type Issue Creation Test",
                        "src/test/resources/testdata/testUpload.txt",
                        "7/Nov/23",
                        "Test description",
                        "Auto Tester 61",
                        "High",
                        Arrays.asList("Test", "Label"),
                        "1h",
                        "1h"
                ),
                new JiraIssueParameters(
                        "TOUCAN project (TOUCAN)",
                        "Improvement",
                        "Improvement Type Issue Creation Test",
                        "src/test/resources/testdata/testUpload.txt",
                        "7/Nov/23",
                        "Test description",
                        "Auto Tester 61",
                        "Low",
                        Arrays.asList("Test", "Label"),
                        "1h",
                        "1h"
                ),
                new JiraIssueParameters(
                        "TOUCAN project (TOUCAN)",
                        "Task",
                        "Task Type Issue Creation Test",
                        "src/test/resources/testdata/testUpload.txt",
                        "7/Nov/23",
                        "Test description",
                        "Auto Tester 61",
                        "Lowest",
                        Arrays.asList("Test", "Label"),
                        "1h",
                        "1h"
                )
        );
    }

    @BeforeAll
    public static void setUp(){

        if (driver == null) {
            System.setProperty("web-driver.chrome.driver", "path_to_chromedriver.exe");
            driver = new ChromeDriver();
        }
        driver.get(BASE_URL);
        try {
            loginTestUser();
        } catch (Exception e) {
            logger.error("Login failed in setUp: " + e.getMessage());
            throw new RuntimeException("Login failed in setUp: " + e.getMessage());
        }
    }
    @BeforeEach
    public void openIssueCreation() {
        driver.findElement(By.id("create-link")).click();
    }

    @AfterEach
    public void afterEach(){

    }

    @AfterAll
    public static void tearDown(){

    }

    @ParameterizedTest
    @MethodSource("testData")
    public void issueCreationTest(JiraIssueParameters params){

    }

    private static void getCredentials() {
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream("config.properties")) {
            prop.load(input);
        } catch (Exception e) {
            logger.error("Failed to load user credentials: " + e.getMessage());
        }

        username = prop.getProperty("username");
        password = prop.getProperty("password");
    }

    private static void loginTestUser() throws Exception{

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
