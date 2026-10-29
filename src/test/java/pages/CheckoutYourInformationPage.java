package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutYourInformationPage extends BasePage {

    private static final By CHECKOUT_INFO_CONTAINER = By.cssSelector("[data-test='checkout-info-container']");
    private static final By FIRST_NAME_INPUT        = By.id("first-name");
    private static final By LAST_NAME_INPUT         = By.id("last-name");
    private static final By POSTAL_CODE_INPUT       = By.id("postal-code");
    private static final By ERROR_MESSAGE           = By.cssSelector("[data-test='error']");
    private static final By ERROR_CLOSE_BUTTON      = By.cssSelector("[data-test='error-button']");
    private static final By CANCEL_BUTTON           = By.id("cancel");
    private static final By CONTINUE_BUTTON         = By.id("continue");

    public CheckoutYourInformationPage(WebDriver driver) {
        super(driver);
    }

    public boolean isCheckoutInformationPageDisplayed() {
        return isVisible(CHECKOUT_INFO_CONTAINER) && driver.getCurrentUrl().contains("checkout-step-one.html");
    }

    public void enterFirstName(String firstName) {
        type(FIRST_NAME_INPUT, firstName);
    }

    public void enterLastName(String lastName) {
        type(LAST_NAME_INPUT, lastName);
    }

    public void enterPostalCode(String postalCode) {
        type(POSTAL_CODE_INPUT, postalCode);
    }

    public void clickContinue() {
        click(CONTINUE_BUTTON);
    }

    public void clickCancel() {
        click(CANCEL_BUTTON);
    }

    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }
}
