package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Checkout: Complete! screen: /checkout-complete.html */
public class CheckoutCompletePage extends BasePage {

    public static final String PATH = "checkout-complete.html";

    public static final By COMPLETE_CONTAINER   = By.cssSelector("[data-test='checkout-complete-container']");
    public static final By PONY_EXPRESS_IMAGE   = By.cssSelector("[data-test='pony-express']");
    public static final By COMPLETE_HEADER      = By.cssSelector("[data-test='complete-header']");
    public static final By COMPLETE_TEXT        = By.cssSelector("[data-test='complete-text']");
    public static final By BACK_HOME_BUTTON     = By.id("back-to-products");
    public static final By GENERATE_PDF_BUTTON  = By.id("generate-pdf-order");

    public CheckoutCompletePage(WebDriver driver) {
        super(driver);
    }

    public CheckoutCompletePage waitUntilLoaded() {
        wait.until(ExpectedConditions.urlContains(PATH));
        waitVisible(COMPLETE_HEADER);
        return this;
    }

    public String getConfirmationHeader() {
        return text(COMPLETE_HEADER);
    }

    public boolean isBackHomeButtonDisplayed() {
        return waitVisible(BACK_HOME_BUTTON).isDisplayed();
    }

    public ProductsPage backHome() {
        click(BACK_HOME_BUTTON);
        return new ProductsPage(driver).waitUntilLoaded();
    }
}
