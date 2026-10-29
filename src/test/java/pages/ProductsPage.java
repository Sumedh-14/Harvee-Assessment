package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

public class ProductsPage extends BasePage {

    private static final By SORT_DROPDOWN        = By.cssSelector("[data-test='product-sort-container']");
    private static final By INVENTORY_LIST       = By.cssSelector("[data-test='inventory-list']");
    private static final By INVENTORY_ITEMS      = By.cssSelector("[data-test='inventory-item']");
    private static final By ITEM_IMAGES          = By.className("inventory_item_img");
    private static final By ITEM_NAMES           = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_DESCRIPTIONS    = By.cssSelector("[data-test='inventory-item-desc']");
    private static final By ITEM_PRICES          = By.cssSelector("[data-test='inventory-item-price']");

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    // Locators that depend on the product name, e.g. "Sauce Labs Backpack"
    private By addToCartButton(String productName) {
        return By.id("add-to-cart-" + toId(productName));
    }

    private By removeButton(String productName) {
        return By.id("remove-" + toId(productName));
    }

    private By productButton(String productName) {
        return By.xpath("//div[@data-test='inventory-item'][.//div[@data-test='inventory-item-name' and normalize-space()='"
                + productName + "']]//button");
    }

    public boolean isProductsPageDisplayed() {
        try {
            wait.until(ExpectedConditions.urlContains("inventory.html"));
        } catch (TimeoutException e) {
            return false;
        }
        return isVisible(INVENTORY_LIST);
    }

    public void selectSortOption(String optionText) {
        new Select(waitForVisible(SORT_DROPDOWN)).selectByVisibleText(optionText);
    }

    public String getSelectedSortOption() {
        return new Select(waitForVisible(SORT_DROPDOWN)).getFirstSelectedOption().getText().trim();
    }

    public List<String> getAllProductNames() {
        waitForVisible(INVENTORY_LIST);
        return getTexts(ITEM_NAMES);
    }

    public List<Double> getAllProductPrices() {
        waitForVisible(INVENTORY_LIST);
        List<Double> prices = new ArrayList<>();
        for (String price : getTexts(ITEM_PRICES)) {
            prices.add(toPrice(price));
        }
        return prices;
    }

    public void clickAddToCart(String productName) {
        click(addToCartButton(productName));
        waitForVisible(removeButton(productName));
    }

    public void clickRemove(String productName) {
        click(removeButton(productName));
        waitForVisible(addToCartButton(productName));
    }

    public String getButtonText(String productName) {
        return getText(productButton(productName));
    }
}
