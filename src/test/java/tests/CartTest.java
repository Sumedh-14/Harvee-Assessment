package tests;

import base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.LoginPage;
import pages.ProductsPage;
import utils.ConfigReader;

import java.util.List;

public class CartTest extends BaseClass {

    private static final String BACKPACK = "Sauce Labs Backpack";
    private static final String BIKE_LIGHT = "Sauce Labs Bike Light";

    @Test(description = "TC10 Add two items and remove one from the products page")
    public void TC10_addTwoItemsAndRemoveOneFromProductsPage() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(ConfigReader.get("standardUser"), ConfigReader.get("password"));

        // Steps 1-3: add two items, check badge and buttons
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.clickAddToCart(BACKPACK);
        productsPage.clickAddToCart(BIKE_LIGHT);

        Assert.assertEquals(productsPage.getCartBadgeCount(), 2, "Badge after adding two items");
        Assert.assertEquals(productsPage.getButtonText(BACKPACK), "Remove");
        Assert.assertEquals(productsPage.getButtonText(BIKE_LIGHT), "Remove");

        // Steps 4-5: remove the Backpack, check badge and button
        productsPage.clickRemove(BACKPACK);

        Assert.assertEquals(productsPage.getCartBadgeCount(), 1, "Badge after removing the Backpack");
        Assert.assertEquals(productsPage.getButtonText(BACKPACK), "Add to cart");

        // Step 6: open the cart
        productsPage.clickCartIcon();

        CartPage cartPage = new CartPage(driver);
        Assert.assertTrue(cartPage.isCartPageDisplayed(), "Cart page should be displayed");
        Assert.assertEquals(cartPage.getCartItemNames(), List.of(BIKE_LIGHT), "Cart contents");
        Assert.assertEquals(cartPage.getCartItemQuantities(), List.of(1), "Quantity");
        Assert.assertEquals(cartPage.getCartItemPrices(), List.of(9.99), "Price");
    }
}
