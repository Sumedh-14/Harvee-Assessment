package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class CheckoutOverviewPage extends BasePage {

    private static final By SUMMARY_CONTAINER    = By.cssSelector("[data-test='checkout-summary-container']");
    private static final By ITEM_QUANTITIES      = By.cssSelector("[data-test='item-quantity']");
    private static final By ITEM_NAMES           = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_PRICES          = By.cssSelector("[data-test='inventory-item-price']");
    private static final By PAYMENT_INFO_VALUE   = By.cssSelector("[data-test='payment-info-value']");
    private static final By SHIPPING_INFO_VALUE  = By.cssSelector("[data-test='shipping-info-value']");
    private static final By ITEM_TOTAL           = By.cssSelector("[data-test='subtotal-label']");
    private static final By TAX                  = By.cssSelector("[data-test='tax-label']");
    private static final By TOTAL                = By.cssSelector("[data-test='total-label']");
    private static final By CANCEL_BUTTON        = By.id("cancel");
    private static final By FINISH_BUTTON        = By.id("finish");

    public CheckoutOverviewPage(WebDriver driver) {
        super(driver);
    }

    public boolean isCheckoutOverviewPageDisplayed() {
        return isVisible(SUMMARY_CONTAINER) && driver.getCurrentUrl().contains("checkout-step-two.html");
    }

    public List<String> getItemNames() {
        waitForVisible(SUMMARY_CONTAINER);
        return getTexts(ITEM_NAMES);
    }

    public List<Double> getItemPrices() {
        waitForVisible(SUMMARY_CONTAINER);
        List<Double> prices = new ArrayList<>();
        for (String price : getTexts(ITEM_PRICES)) {
            prices.add(toPrice(price));
        }
        return prices;
    }

    /** "Item total: $39.98" -> 39.98 */
    public double getItemTotal() {
        return toPrice(getText(ITEM_TOTAL));
    }

    /** "Tax: $3.20" -> 3.20 */
    public double getTax() {
        return toPrice(getText(TAX));
    }

    /** "Total: $43.18" -> 43.18 */
    public double getTotal() {
        return toPrice(getText(TOTAL));
    }

    public void clickFinish() {
        click(FINISH_BUTTON);
    }

    public void clickCancel() {
        click(CANCEL_BUTTON);
    }
}
