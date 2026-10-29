package base;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.ConfigReader;
import utils.DriverFactory;
import utils.HtmlReportListener;
import utils.ScreenshotUtil;

import java.nio.file.Path;
import java.time.Duration;

public class BaseClass {

    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        driver = DriverFactory.create();
        openApplication();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (driver == null) return;
        if (result.getStatus() == ITestResult.FAILURE) {
            Path screenshot = ScreenshotUtil.capture(driver, result.getMethod().getMethodName());
            result.setAttribute(HtmlReportListener.SCREENSHOT_ATTRIBUTE, screenshot);
        }
        driver.quit();
    }

    // Reloads once if the site comes up blank on the first load.
    private void openApplication() {
        driver.get(ConfigReader.get("baseUrl"));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("timeoutSeconds")));
        By loginButton = By.id("login-button");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(loginButton));
        } catch (TimeoutException e) {
            driver.navigate().refresh();
            wait.until(ExpectedConditions.visibilityOfElementLocated(loginButton));
        }
    }
}
