package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.math.BigDecimal;
import java.util.List;

public class CheckoutOverviewPage extends BasePage {

    public static final String PATH = "checkout-step-two.html";

    public static final By SUMMARY_CONTAINER    = By.cssSelector("[data-test='checkout-summary-container']");
    public static final By CART_LIST            = By.cssSelector("[data-test='cart-list']");
    public static final By QTY_LABEL            = By.cssSelector("[data-test='cart-quantity-label']");
    public static final By DESCRIPTION_LABEL    = By.cssSelector("[data-test='cart-desc-label']");
    public static final By ORDER_ITEMS          = By.cssSelector("[data-test='inventory-item']");
    public static final By ITEM_QUANTITIES      = By.cssSelector("[data-test='item-quantity']");
    public static final By ITEM_NAMES           = By.cssSelector("[data-test='inventory-item-name']");
    public static final By ITEM_DESCRIPTIONS    = By.cssSelector("[data-test='inventory-item-desc']");
    public static final By ITEM_PRICES          = By.cssSelector("[data-test='inventory-item-price']");
    public static final By PAYMENT_INFO_LABEL   = By.cssSelector("[data-test='payment-info-label']");
    public static final By PAYMENT_INFO_VALUE   = By.cssSelector("[data-test='payment-info-value']");
    public static final By SHIPPING_INFO_LABEL  = By.cssSelector("[data-test='shipping-info-label']");
    public static final By SHIPPING_INFO_VALUE  = By.cssSelector("[data-test='shipping-info-value']");
    public static final By PRICE_TOTAL_LABEL    = By.cssSelector("[data-test='total-info-label']");
    public static final By ITEM_TOTAL           = By.cssSelector("[data-test='subtotal-label']");
    public static final By TAX                  = By.cssSelector("[data-test='tax-label']");
    public static final By TOTAL                = By.cssSelector("[data-test='total-label']");
    public static final By CANCEL_BUTTON        = By.id("cancel");
    public static final By FINISH_BUTTON        = By.id("finish");

    public CheckoutOverviewPage(WebDriver driver) {
        super(driver);
    }

    public static By orderItem(String productName) {
        return By.xpath("//div[@data-test='inventory-item'][.//div[@data-test='inventory-item-name' and normalize-space()='"
                + productName + "']]");
    }

    // ---------- Actions ----------

    public CheckoutOverviewPage waitUntilLoaded() {
        wait.until(ExpectedConditions.urlContains(PATH));
        waitVisible(SUMMARY_CONTAINER);
        return this;
    }

    public List<String> getItemNames() {
        return texts(ITEM_NAMES);
    }

    public List<BigDecimal> getItemPrices() {
        return texts(ITEM_PRICES).stream().map(BasePage::money).toList();
    }

    public BigDecimal getItemTotal() {
        return money(text(ITEM_TOTAL));
    }

    public BigDecimal getTax() {
        return money(text(TAX));
    }

    public BigDecimal getTotal() {
        return money(text(TOTAL));
    }

    public CheckoutCompletePage finish() {
        click(FINISH_BUTTON);
        return new CheckoutCompletePage(driver).waitUntilLoaded();
    }
}
