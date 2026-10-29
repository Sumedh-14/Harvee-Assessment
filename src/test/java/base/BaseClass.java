package base;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pages.LoginPage;
import pages.ProductsPage;
import utils.ConfigReader;
import utils.DriverFactory;
import utils.HtmlReportListener;
import utils.ScreenshotUtil;

import java.nio.file.Path;
import java.util.Arrays;

/**
 * Every test gets its own browser, so tests run independently, in any order and in parallel.
 */
public class BaseClass {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    protected WebDriver getDriver() {
        return DRIVER.get();
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DRIVER.set(DriverFactory.create());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        WebDriver driver = DRIVER.get();
        if (driver == null) return;
        try {
            if (result.getStatus() == ITestResult.FAILURE) {
                String params = result.getParameters().length == 0 ? "" : "_" + Arrays.toString(result.getParameters());
                Path screenshot = ScreenshotUtil.capture(driver, result.getMethod().getMethodName() + params);
                result.setAttribute(HtmlReportListener.SCREENSHOT_ATTRIBUTE, screenshot);
            }
        } finally {
            driver.quit();
            DRIVER.remove();
        }
    }

    // ---------- Shared preconditions ----------

    protected LoginPage openLoginPage() {
        return new LoginPage(getDriver()).open();
    }

    protected ProductsPage loginAsStandardUser() {
        return openLoginPage().loginAs(ConfigReader.get("standardUser"), ConfigReader.get("password"));
    }
}
