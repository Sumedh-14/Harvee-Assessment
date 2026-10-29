package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import utils.ConfigReader;

import java.math.BigDecimal;
import java.util.List;

/** Products (inventory) screen: /inventory.html */
public class ProductsPage extends BasePage {

    public static final String PATH = "inventory.html";

    public static final By SORT_DROPDOWN        = By.cssSelector("[data-test='product-sort-container']");
    public static final By SORT_ACTIVE_OPTION   = By.cssSelector("[data-test='active-option']");
    public static final String SORT_NAME_A_TO_Z   = "az";
    public static final String SORT_NAME_Z_TO_A   = "za";
    public static final String SORT_PRICE_LOW_HIGH = "lohi";
    public static final String SORT_PRICE_HIGH_LOW = "hilo";
    public static final By INVENTORY_CONTAINER  = By.cssSelector("[data-test='inventory-container']");
    public static final By INVENTORY_LIST       = By.cssSelector("[data-test='inventory-list']");
    public static final By INVENTORY_ITEMS      = By.cssSelector("[data-test='inventory-item']");
    public static final By ITEM_IMAGES          = By.className("inventory_item_img");
    public static final By ITEM_NAMES           = By.cssSelector("[data-test='inventory-item-name']");
    public static final By ITEM_DESCRIPTIONS    = By.cssSelector("[data-test='inventory-item-desc']");
    public static final By ITEM_PRICES          = By.cssSelector("[data-test='inventory-item-price']");
    public static final By ITEM_BUTTONS         = By.cssSelector(".inventory_item button");

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    public static By addToCartButton(String productName) {
        return By.id("add-to-cart-" + toSlug(productName));
    }

    public static By removeButton(String productName) {
        return By.id("remove-" + toSlug(productName));
    }

    public static By productImage(String productName) {
        return By.cssSelector("[data-test='inventory-item-" + toSlug(productName) + "-img']");
    }

    public static By productCard(String productName) {
        return By.xpath("//div[@data-test='inventory-item'][.//div[@data-test='inventory-item-name' and normalize-space()='"
                + productName + "']]");
    }

    public static By productNameLink(String productName) {
        return By.xpath("//div[@data-test='inventory-item-name' and normalize-space()='" + productName + "']/parent::a");
    }

    // ---------- Actions ----------

    public ProductsPage waitUntilLoaded() {
        wait.until(ExpectedConditions.urlContains(PATH));
        waitVisible(INVENTORY_LIST);
        return this;
    }

    /** Navigates straight to /inventory.html without going through the login form. */
    public void openByUrl() {
        driver.get(ConfigReader.get("baseUrl") + PATH);
    }

    public ProductsPage sortBy(String optionValue) {
        new Select(waitVisible(SORT_DROPDOWN)).selectByValue(optionValue);
        return this;
    }

    public String getActiveSortOption() {
        return new Select(waitVisible(SORT_DROPDOWN)).getFirstSelectedOption().getText().trim();
    }

    public List<String> getProductNames() {
        return texts(ITEM_NAMES);
    }

    public List<BigDecimal> getProductPrices() {
        return texts(ITEM_PRICES).stream().map(BasePage::money).toList();
    }

    public ProductsPage addToCart(String productName) {
        click(addToCartButton(productName));
        waitVisible(removeButton(productName));
        return this;
    }

    /** Text of the product's cart button: "Add to cart" or "Remove". */
    public String getCartButtonText(String productName) {
        return waitVisible(productCard(productName)).findElement(By.tagName("button")).getText().trim();
    }

    public ProductsPage removeFromCart(String productName) {
        click(removeButton(productName));
        waitVisible(addToCartButton(productName));
        return this;
    }
}
