package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CheckoutYourInformationPage extends BasePage {

    public static final String PATH = "checkout-step-one.html";

    public static final By CHECKOUT_INFO_CONTAINER = By.cssSelector("[data-test='checkout-info-container']");
    public static final By FIRST_NAME_INPUT        = By.id("first-name");
    public static final By LAST_NAME_INPUT         = By.id("last-name");
    public static final By POSTAL_CODE_INPUT       = By.id("postal-code");
    public static final By ERROR_MESSAGE           = By.cssSelector("[data-test='error']");
    public static final By ERROR_CLOSE_BUTTON      = By.cssSelector("[data-test='error-button']");
    public static final By CANCEL_BUTTON           = By.id("cancel");
    public static final By CONTINUE_BUTTON         = By.id("continue");

    public CheckoutYourInformationPage(WebDriver driver) {
        super(driver);
    }

    // ---------- Actions ----------

    public CheckoutYourInformationPage waitUntilLoaded() {
        wait.until(ExpectedConditions.urlContains(PATH));
        waitVisible(CHECKOUT_INFO_CONTAINER);
        return this;
    }

    public CheckoutYourInformationPage fillInformation(String firstName, String lastName, String postalCode) {
        type(FIRST_NAME_INPUT, firstName);
        type(LAST_NAME_INPUT, lastName);
        type(POSTAL_CODE_INPUT, postalCode);
        return this;
    }

    public CheckoutOverviewPage continueToOverview() {
        click(CONTINUE_BUTTON);
        return new CheckoutOverviewPage(driver).waitUntilLoaded();
    }

    /** Clicks Continue and expects to stay on this page with a validation error. */
    public CheckoutYourInformationPage continueExpectingError() {
        click(CONTINUE_BUTTON);
        waitVisible(ERROR_MESSAGE);
        return this;
    }

    public boolean isDisplayed() {
        return driver.getCurrentUrl().contains(PATH) && !driver.findElements(CONTINUE_BUTTON).isEmpty();
    }

    public String getErrorMessage() {
        return text(ERROR_MESSAGE);
    }
}
