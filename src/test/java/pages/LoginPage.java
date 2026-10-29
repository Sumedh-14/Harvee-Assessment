package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import utils.ConfigReader;

public class LoginPage extends BasePage {

    public static final By LOGIN_LOGO          = By.className("login_logo");
    public static final By USERNAME_INPUT      = By.id("user-name");
    public static final By PASSWORD_INPUT      = By.id("password");
    public static final By LOGIN_BUTTON        = By.id("login-button");
    public static final By ERROR_MESSAGE       = By.cssSelector("[data-test='error']");
    public static final By ERROR_CLOSE_BUTTON  = By.cssSelector("[data-test='error-button']");
    public static final By CREDENTIALS_INFO    = By.cssSelector("[data-test='login-credentials']");
    public static final By PASSWORD_INFO       = By.cssSelector("[data-test='login-password']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /** Opens the base URL; reloads once if the app stays blank (seen intermittently on the first load). */
    public LoginPage open() {
        driver.get(ConfigReader.get("baseUrl"));
        try {
            return waitUntilLoaded();
        } catch (TimeoutException e) {
            driver.navigate().refresh();
            return waitUntilLoaded();
        }
    }

    public LoginPage waitUntilLoaded() {
        waitVisible(LOGIN_BUTTON);
        return this;
    }

    public boolean isDisplayed() {
        return !driver.findElements(LOGIN_BUTTON).isEmpty() && driver.findElement(LOGIN_BUTTON).isDisplayed();
    }

    /** Logs in and expects to land on the products page. */
    public ProductsPage loginAs(String username, String password) {
        submit(username, password);
        return new ProductsPage(driver).waitUntilLoaded();
    }

    /** Logs in and expects to stay on the login page with an error. */
    public LoginPage loginExpectingError(String username, String password) {
        submit(username, password);
        waitVisible(ERROR_MESSAGE);
        return this;
    }

    public String getErrorMessage() {
        return text(ERROR_MESSAGE);
    }

    private void submit(String username, String password) {
        type(USERNAME_INPUT, username);
        type(PASSWORD_INPUT, password);
        click(LOGIN_BUTTON);
    }
}
