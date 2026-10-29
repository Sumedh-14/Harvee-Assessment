package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BasePage {

    private static final Pattern MONEY = Pattern.compile("\\$?(\\d+\\.\\d{2})");

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    public static final By APP_LOGO            = By.className("app_logo");
    public static final By PAGE_TITLE          = By.cssSelector("[data-test='title']");
    public static final By SHOPPING_CART_LINK  = By.cssSelector("[data-test='shopping-cart-link']");
    public static final By SHOPPING_CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");
    public static final By MENU_OPEN_BUTTON    = By.id("react-burger-menu-btn");
    public static final By MENU_CLOSE_BUTTON   = By.id("react-burger-cross-btn");
    public static final By MENU_ALL_ITEMS      = By.id("inventory_sidebar_link");
    public static final By MENU_ABOUT          = By.id("about_sidebar_link");
    public static final By MENU_LOGOUT         = By.id("logout_sidebar_link");
    public static final By MENU_RESET_APP      = By.id("reset_sidebar_link");
    public static final By FOOTER              = By.cssSelector("[data-test='footer']");
    public static final By FOOTER_TWITTER_X    = By.cssSelector("[data-test='social-x']");
    public static final By FOOTER_FACEBOOK     = By.cssSelector("[data-test='social-facebook']");
    public static final By FOOTER_LINKEDIN     = By.cssSelector("[data-test='social-linkedin']");
    public static final By FOOTER_COPYRIGHT    = By.cssSelector("[data-test='footer-copy']");

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("timeoutSeconds")));
    }

    // ---------- Header / menu actions ----------

    public String getPageTitle() {
        return text(PAGE_TITLE);
    }

    /** Number shown on the cart icon; 0 when the badge is not displayed. */
    public int getCartBadgeCount() {
        List<WebElement> badge = driver.findElements(SHOPPING_CART_BADGE);
        return badge.isEmpty() ? 0 : Integer.parseInt(badge.get(0).getText().trim());
    }

    public CartPage openCart() {
        click(SHOPPING_CART_LINK);
        return new CartPage(driver).waitUntilLoaded();
    }

    public LoginPage logout() {
        click(MENU_OPEN_BUTTON);
        click(MENU_LOGOUT);
        return new LoginPage(driver).waitUntilLoaded();
    }

    // ---------- Wait-based helpers ----------

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Retries the click until it lands, e.g. while the side menu is still sliding in. */
    protected void click(By locator) {
        wait.ignoring(ElementClickInterceptedException.class)
            .ignoring(ElementNotInteractableException.class)
            .ignoring(StaleElementReferenceException.class)
            .until(d -> {
                d.findElement(locator).click();
                return true;
            });
    }

    protected void type(By locator, String text) {
        WebElement field = waitVisible(locator);
        field.clear();
        if (text != null && !text.isEmpty()) field.sendKeys(text);
    }

    protected String text(By locator) {
        return waitVisible(locator).getText().trim();
    }

    protected List<String> texts(By locator) {
        return driver.findElements(locator).stream().map(e -> e.getText().trim()).toList();
    }

    /** Extracts the amount from text such as "$29.99" or "Item total: $39.98". */
    protected static BigDecimal money(String text) {
        Matcher m = MONEY.matcher(text);
        if (!m.find()) throw new IllegalArgumentException("No amount in: " + text);
        return new BigDecimal(m.group(1));
    }

    /** Converts a product name to the slug used in SauceDemo button ids, e.g. "Sauce Labs Backpack" -> "sauce-labs-backpack". */
    protected static String toSlug(String productName) {
        return productName.trim().toLowerCase().replace(" ", "-");
    }
}
