package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutCompletePage extends BasePage {

    private static final By COMPLETE_HEADER     = By.cssSelector("[data-test='complete-header']");
    private static final By COMPLETE_TEXT       = By.cssSelector("[data-test='complete-text']");
    private static final By PONY_EXPRESS_IMAGE  = By.cssSelector("[data-test='pony-express']");
    private static final By BACK_HOME_BUTTON    = By.id("back-to-products");

    public CheckoutCompletePage(WebDriver driver) {
        super(driver);
    }

    public boolean isCheckoutCompletePageDisplayed() {
        return isVisible(COMPLETE_HEADER) && driver.getCurrentUrl().contains("checkout-complete.html");
    }

    public String getConfirmationMessage() {
        return getText(COMPLETE_HEADER);
    }

    public boolean isBackHomeButtonDisplayed() {
        return isVisible(BACK_HOME_BUTTON);
    }

    public void clickBackHome() {
        click(BACK_HOME_BUTTON);
    }
}
