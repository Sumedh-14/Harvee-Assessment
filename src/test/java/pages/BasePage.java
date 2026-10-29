package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Parent of all page classes: header / menu / footer locators and their actions,
 * plus the wait helpers every page uses (no Thread.sleep anywhere).
 */
public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    // Header
    private static final By PAGE_TITLE          = By.cssSelector("[data-test='title']");
    private static final By SHOPPING_CART_LINK  = By.cssSelector("[data-test='shopping-cart-link']");
    private static final By SHOPPING_CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");

    // Side menu
    private static final By MENU_OPEN_BUTTON    = By.id("react-burger-menu-btn");
    private static final By MENU_LOGOUT         = By.id("logout_sidebar_link");
    private static final By MENU_RESET_APP      = By.id("reset_sidebar_link");

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("timeoutSeconds")));
    }

    // ---------- Header / menu actions ----------

    public String getPageTitle() {
        return getText(PAGE_TITLE);
    }

    /** Number on the cart icon; 0 when no badge is shown. */
    public int getCartBadgeCount() {
        List<WebElement> badge = driver.findElements(SHOPPING_CART_BADGE);
        if (badge.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(badge.get(0).getText().trim());
    }

    public void clickCartIcon() {
        click(SHOPPING_CART_LINK);
    }

    public void openMenu() {
        click(MENU_OPEN_BUTTON);
    }

    public void clickLogout() {
        click(MENU_LOGOUT);
    }

    public void clickResetAppState() {
        click(MENU_RESET_APP);
    }

    public void logout() {
        openMenu();
        clickLogout();
    }

    // ---------- Helpers used by all pages ----------

    /** Waits until the element is visible and returns it. */
    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Waits up to the timeout for the element; returns false instead of failing if it never appears. */
    protected boolean isVisible(By locator) {
        try {
            waitForVisible(locator);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Clicks the element, retrying until the click works: the element may not be on the page yet
     * (page still loading) or not clickable yet (side menu still sliding open).
     */
    protected void click(By locator) {
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(ConfigReader.getInt("timeoutSeconds")))
                .pollingEvery(Duration.ofMillis(250))
                .ignoring(NoSuchElementException.class)
                .ignoring(ElementClickInterceptedException.class)
                .ignoring(ElementNotInteractableException.class)
                .ignoring(StaleElementReferenceException.class)
                .until(d -> {
                    d.findElement(locator).click();
                    return true;
                });
    }

    protected void type(By locator, String value) {
        WebElement field = waitForVisible(locator);
        field.clear();
        field.sendKeys(value);
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    /** Texts of all elements matching the locator, top to bottom. */
    protected List<String> getTexts(By locator) {
        List<String> texts = new ArrayList<>();
        for (WebElement element : driver.findElements(locator)) {
            texts.add(element.getText().trim());
        }
        return texts;
    }

    /** "$29.99" or "Item total: $39.98" -> 29.99 / 39.98 */
    protected static double toPrice(String text) {
        return Double.parseDouble(text.substring(text.indexOf('$') + 1).trim());
    }

    /** "Sauce Labs Backpack" -> "sauce-labs-backpack" (used in the button ids). */
    protected static String toId(String productName) {
        return productName.trim().toLowerCase().replace(" ", "-");
    }
}
