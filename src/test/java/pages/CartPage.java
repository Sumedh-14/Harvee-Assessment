package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

public class CartPage extends BasePage {

    private static final By CART_LIST                = By.cssSelector("[data-test='cart-list']");
    private static final By QTY_LABEL                = By.cssSelector("[data-test='cart-quantity-label']");
    private static final By DESCRIPTION_LABEL        = By.cssSelector("[data-test='cart-desc-label']");
    private static final By CART_ITEMS               = By.cssSelector("[data-test='inventory-item']");
    private static final By ITEM_QUANTITIES          = By.cssSelector("[data-test='item-quantity']");
    private static final By ITEM_NAMES               = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_DESCRIPTIONS        = By.cssSelector("[data-test='inventory-item-desc']");
    private static final By ITEM_PRICES              = By.cssSelector("[data-test='inventory-item-price']");
    private static final By CONTINUE_SHOPPING_BUTTON = By.id("continue-shopping");
    private static final By CHECKOUT_BUTTON          = By.id("checkout");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    private By removeButton(String productName) {
        return By.id("remove-" + toId(productName));
    }

    public boolean isCartPageDisplayed() {
        return isVisible(CART_LIST) && driver.getCurrentUrl().contains("cart.html");
    }

    public List<String> getCartItemNames() {
        waitForVisible(CART_LIST);
        return getTexts(ITEM_NAMES);
    }

    public List<Integer> getCartItemQuantities() {
        waitForVisible(CART_LIST);
        List<Integer> quantities = new ArrayList<>();
        for (String qty : getTexts(ITEM_QUANTITIES)) {
            quantities.add(Integer.parseInt(qty));
        }
        return quantities;
    }

    public List<Double> getCartItemPrices() {
        waitForVisible(CART_LIST);
        List<Double> prices = new ArrayList<>();
        for (String price : getTexts(ITEM_PRICES)) {
            prices.add(toPrice(price));
        }
        return prices;
    }

    public boolean isCartEmpty() {
        waitForVisible(CART_LIST);
        return driver.findElements(CART_ITEMS).isEmpty();
    }

    /** Clicks "Remove" on the item and waits until it disappears from the cart. */
    public void clickRemove(String productName) {
        click(removeButton(productName));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(removeButton(productName)));
    }

    public void clickContinueShopping() {
        click(CONTINUE_SHOPPING_BUTTON);
    }

    public void clickCheckout() {
        click(CHECKOUT_BUTTON);
    }
}
