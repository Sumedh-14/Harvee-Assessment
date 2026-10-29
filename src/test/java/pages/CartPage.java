package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.math.BigDecimal;
import java.util.List;

public class CartPage extends BasePage {

    public static final String PATH = "cart.html";

    public static final By CART_CONTENTS        = By.cssSelector("[data-test='cart-contents-container']");
    public static final By CART_LIST            = By.cssSelector("[data-test='cart-list']");
    public static final By QTY_LABEL            = By.cssSelector("[data-test='cart-quantity-label']");
    public static final By DESCRIPTION_LABEL    = By.cssSelector("[data-test='cart-desc-label']");
    public static final By CART_ITEMS           = By.cssSelector("[data-test='inventory-item']");
    public static final By ITEM_QUANTITIES      = By.cssSelector("[data-test='item-quantity']");
    public static final By ITEM_NAMES           = By.cssSelector("[data-test='inventory-item-name']");
    public static final By ITEM_DESCRIPTIONS    = By.cssSelector("[data-test='inventory-item-desc']");
    public static final By ITEM_PRICES          = By.cssSelector("[data-test='inventory-item-price']");
    public static final By REMOVE_BUTTONS       = By.cssSelector(".cart_item button");
    public static final By CONTINUE_SHOPPING_BUTTON = By.id("continue-shopping");
    public static final By CHECKOUT_BUTTON      = By.id("checkout");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public static By removeButton(String productName) {
        return By.id("remove-" + toSlug(productName));
    }

    public static By cartItem(String productName) {
        return By.xpath("//div[@data-test='inventory-item'][.//div[@data-test='inventory-item-name' and normalize-space()='"
                + productName + "']]");
    }

    // ---------- Actions ----------

    public CartPage waitUntilLoaded() {
        wait.until(ExpectedConditions.urlContains(PATH));
        waitVisible(CART_LIST);
        return this;
    }

    public List<String> getItemNames() {
        return texts(ITEM_NAMES);
    }

    public List<Integer> getItemQuantities() {
        return texts(ITEM_QUANTITIES).stream().map(Integer::parseInt).toList();
    }

    public List<BigDecimal> getItemPrices() {
        return texts(ITEM_PRICES).stream().map(BasePage::money).toList();
    }

    public boolean isEmpty() {
        return driver.findElements(CART_ITEMS).isEmpty();
    }

    public CartPage removeItem(String productName) {
        click(removeButton(productName));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(cartItem(productName)));
        return this;
    }

    public CheckoutYourInformationPage checkout() {
        click(CHECKOUT_BUTTON);
        return new CheckoutYourInformationPage(driver).waitUntilLoaded();
    }
}
