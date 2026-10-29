package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By LOGIN_LOGO          = By.className("login_logo");
    private static final By USERNAME_INPUT      = By.id("user-name");
    private static final By PASSWORD_INPUT      = By.id("password");
    private static final By LOGIN_BUTTON        = By.id("login-button");
    private static final By ERROR_MESSAGE       = By.cssSelector("[data-test='error']");
    private static final By ERROR_CLOSE_BUTTON  = By.cssSelector("[data-test='error-button']");
    private static final By CREDENTIALS_INFO    = By.cssSelector("[data-test='login-credentials']");
    private static final By PASSWORD_INFO       = By.cssSelector("[data-test='login-password']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void enterUserName(String userName) {
        type(USERNAME_INPUT, userName);
    }

    public void enterPassword(String password) {
        type(PASSWORD_INPUT, password);
    }

    public void clickLogin() {
        click(LOGIN_BUTTON);
    }

    /** Enter user name + password and click Login. */
    public void login(String userName, String password) {
        enterUserName(userName);
        enterPassword(password);
        clickLogin();
    }

    public boolean isLoginPageDisplayed() {
        return isVisible(LOGIN_BUTTON);
    }

    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    public void closeErrorMessage() {
        click(ERROR_CLOSE_BUTTON);
    }
}
