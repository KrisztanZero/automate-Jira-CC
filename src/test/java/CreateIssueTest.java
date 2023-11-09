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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Duration;
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
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("create_link"))).click();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.error("Thread sleep interrupted: " + e.getMessage());
        }
    }

    @AfterEach
    public void afterEach(){
        sleepSeconds(2);
    }

    @AfterAll
    public static void tearDown(){
    }

    @ParameterizedTest
    @MethodSource("testdata")
    public void issueCreationTest(JiraIssueParameters params) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));

        selectProject(params, wait);
        sleepSeconds(1);
        selectIssueType(params, wait);
        sleepSeconds(1);
        addSummary(params, wait);
        sleepSeconds(1);
        setDueDate(params, wait);
        sleepSeconds(1);
        clickAssignToMe(wait);
        selectPriority(params, wait);
        sleepSeconds(1);
        setLabels(params, wait);
        sleepSeconds(1);
        setOriginalEstimate(params, wait);
        sleepSeconds(1);
        setRemainingEstimate(params, wait);
        sleepSeconds(1);
        clickCreateIssueSubmit(wait);

        waitForSuccessMessage(wait);
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

    private void sleepSeconds(int seconds) {
        try {
            logger.info("Sleeping for {} seconds...", seconds);
            Thread.sleep(seconds * 1000);
            logger.info("Awake after sleep.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("InterruptedException during sleep: " + e.getMessage());
        }
    }

    private void selectProject(JiraIssueParameters params, WebDriverWait wait) {
        try {
            By projectInputLocator = By.cssSelector("input#project-field");
            WebElement projectInput = wait.until(ExpectedConditions.elementToBeClickable(projectInputLocator));

            logger.info("Before Clear Project field: " + projectInput.getAttribute("value"));
            projectInput.click();
            sleepSeconds(1);

            while (!projectInput.getAttribute("value").isEmpty()) {
                projectInput.sendKeys(Keys.BACK_SPACE);
            }
            sleepSeconds(1);

            logger.info("After Clear Project Field: " + projectInput.getAttribute("value"));

            projectInput.sendKeys(params.getProjectName());
            sleepSeconds(1);
            logger.info("After sendKeys(params.getProjectName() : " + projectInput.getAttribute("value"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].blur();", projectInput);
            sleepSeconds(1);
        } catch (WebDriverException e) {
            logger.error("Exception while interacting with project input: " + e.getMessage());
        }
    }

    private void selectIssueType(JiraIssueParameters params, WebDriverWait wait) {
        try {
            By issueTypeInputLocator = By.cssSelector("input#issuetype-field");
            WebElement issueTypeInput = wait.until(ExpectedConditions.elementToBeClickable(issueTypeInputLocator));

            logger.info("Before Clear Type Field: " + issueTypeInput.getAttribute("value"));
            issueTypeInput.click();
            sleepSeconds(1);
            while (!issueTypeInput.getAttribute("value").isEmpty()) {
                issueTypeInput.sendKeys(Keys.BACK_SPACE);
            }

            logger.info("After Clear Type Field: " + issueTypeInput.getAttribute("value"));
            sleepSeconds(1);
            issueTypeInput.sendKeys(params.getIssueType());
            sleepSeconds(1);
            logger.info("After sendKeys(params.getIssueType(): " +issueTypeInput.getAttribute("value"));

            ((JavascriptExecutor) driver).executeScript("arguments[0].blur();", issueTypeInput);
            sleepSeconds(1);
        } catch (WebDriverException e) {
            logger.error("Exception while interacting with issue type input: " + e.getMessage());
        }
    }

    private void addSummary(JiraIssueParameters params, WebDriverWait wait) {
        try {
            By summaryFieldLocator = By.cssSelector("input#summary");
            WebElement summaryField = wait.until(ExpectedConditions.elementToBeClickable(summaryFieldLocator));

            logger.info("Before Clear Summary Field: " + summaryField.getAttribute("value"));
            summaryField.click();
            sleepSeconds(1);

            while (!summaryField.getAttribute("value").isEmpty()) {
                summaryField.sendKeys(Keys.BACK_SPACE);
            }
            sleepSeconds(1);
            logger.info("After Clear Summary Field: " + summaryField.getAttribute("value"));
            summaryField.sendKeys(params.getSummary());
            sleepSeconds(1);

            logger.info("After sendKeys(params.getSummary() : " + summaryField.getAttribute("value"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].blur();", summaryField);
            sleepSeconds(1);
        } catch (WebDriverException e) {
            logger.error("Exception while interacting with the summary field: " + e.getMessage());
        }
    }

    private void setDueDate(JiraIssueParameters params, WebDriverWait wait) {
        try {
            By dueDateFieldLocator = By.cssSelector("input#duedate");
            WebElement dueDateField = wait.until(ExpectedConditions.elementToBeClickable(dueDateFieldLocator));

            logger.info("Before Clear (Due Date): " + dueDateField.getAttribute("value"));

            dueDateField.clear();

            logger.info("After Clear (Due Date): " + dueDateField.getAttribute("value"));

            dueDateField.sendKeys(params.getDueDate());

            logger.info("After Set (Due Date): " + dueDateField.getAttribute("value"));

            ((JavascriptExecutor) driver).executeScript("arguments[0].blur();", dueDateField);
            sleepSeconds(1);
        } catch (WebDriverException e) {
            logger.error("Exception while interacting with Due Date field: " + e.getMessage());
        }
    }

    private void clickAssignToMe(WebDriverWait wait) {
        try {
            By assignToMeTriggerLocator = By.cssSelector("button#assign-to-me-trigger");
            WebElement assignToMeTrigger = wait.until(ExpectedConditions.elementToBeClickable(assignToMeTriggerLocator));
            sleepSeconds(1);
            logger.info("Before Clicking Assign To Me Trigger");

            assignToMeTrigger.click();
            sleepSeconds(1);

            logger.info("After Clicking Assign To Me Trigger");
        } catch (WebDriverException e) {
            logger.error("Exception while clicking Assign To Me Trigger: " + e.getMessage());
        }
    }

    private void selectPriority(JiraIssueParameters params, WebDriverWait wait) {
        try {
            By priorityInputLocator = By.cssSelector("input#priority-field");
            WebElement priorityInput = wait.until(ExpectedConditions.elementToBeClickable(priorityInputLocator));

            logger.info("Before Entering Priority: " + priorityInput.getAttribute("value"));
            priorityInput.click();
            sleepSeconds(1);
            while (!priorityInput.getAttribute("value").isEmpty()) {
                priorityInput.sendKeys(Keys.BACK_SPACE);
            }
            sleepSeconds(1);

            logger.info("After Clearing Priority: " + priorityInput.getAttribute("value"));

            priorityInput.sendKeys(params.getPriority());
            sleepSeconds(1);

            logger.info("After Entering Priority: " + priorityInput.getAttribute("value"));

            ((JavascriptExecutor) driver).executeScript("arguments[0].blur();", priorityInput);
            sleepSeconds(1);
        } catch (WebDriverException e) {
            logger.error("Exception while interacting with Priority field: " + e.getMessage());
        }
    }

    private void setLabels(JiraIssueParameters params, WebDriverWait wait) {
        try {
            By labelsFieldLocator = By.cssSelector("textarea#labels-textarea");
            WebElement labelsField = wait.until(ExpectedConditions.elementToBeClickable(labelsFieldLocator));

            logger.info("Before Entering Labels: " + labelsField.getAttribute("value"));
            labelsField.click();
            sleepSeconds(1);
            labelsField.sendKeys(String.join(" ", params.getLabels()));
            sleepSeconds(1);

            logger.info("After Entering Labels: " + labelsField.getAttribute("value"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].blur();", labelsField);
            sleepSeconds(1);
        } catch (WebDriverException e) {
            logger.error("Exception while entering Labels: " + e.getMessage());
        }
    }

    private void setOriginalEstimate(JiraIssueParameters params, WebDriverWait wait) {
        try {
            By originalEstimateFieldLocator = By.cssSelector("input#timetracking_originalestimate");
            WebElement originalEstimateField = wait.until(ExpectedConditions.elementToBeClickable(originalEstimateFieldLocator));

            logger.info("Before Entering Original Estimate: " + originalEstimateField.getAttribute("value"));
            originalEstimateField.click();
            sleepSeconds(1);
            originalEstimateField.sendKeys(params.getOriginalEstimate());
            sleepSeconds(1);

            logger.info("After Entering Original Estimate: " + originalEstimateField.getAttribute("value"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].blur();", originalEstimateField);
            sleepSeconds(1);
        } catch (WebDriverException e) {
            logger.error("Exception while entering Original Estimate: " + e.getMessage());
        }
    }

    private void setRemainingEstimate(JiraIssueParameters params, WebDriverWait wait) {
        try {
            By remainingEstimateFieldLocator = By.cssSelector("input#timetracking_remainingestimate");
            WebElement remainingEstimateField = wait.until(ExpectedConditions.elementToBeClickable(remainingEstimateFieldLocator));

            logger.info("Before Entering Remaining Estimate: " + remainingEstimateField.getAttribute("value"));
            remainingEstimateField.click();
            sleepSeconds(1);
            remainingEstimateField.sendKeys(params.getRemainingEstimate());
            sleepSeconds(1);

            logger.info("After Entering Remaining Estimate: " + remainingEstimateField.getAttribute("value"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].blur();", remainingEstimateField);
            sleepSeconds(1);
        } catch (WebDriverException e) {
            logger.error("Exception while entering Remaining Estimate: " + e.getMessage());
        }
    }

    private void clickCreateIssueSubmit(WebDriverWait wait) {
        try {
            By createIssueSubmitButtonLocator = By.cssSelector("input#create-issue-submit");
            WebElement createIssueSubmitButton = wait.until(ExpectedConditions.elementToBeClickable(createIssueSubmitButtonLocator));

            logger.info("Before Clicking Create Issue Submit Button");

            createIssueSubmitButton.click();

            logger.info("After Clicking Create Issue Submit Button");
        } catch (WebDriverException e) {
            logger.error("Exception while clicking Create Issue Submit Button: " + e.getMessage());
        }
    }

    private void waitForSuccessMessage(WebDriverWait wait) {
        try {
            WebElement successMessage = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("aui-message-success")));
            logger.info("Success Message: " + successMessage.getText());
        } catch (WebDriverException e) {
            logger.error("Exception while waiting for success message: " + e.getMessage());
        }
    }
}
