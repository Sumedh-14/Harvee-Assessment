package tests;

import base.BaseClass;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.ProductsPage;

import java.math.BigDecimal;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class CartTest extends BaseClass {

    private static final String BACKPACK = "Sauce Labs Backpack";
    private static final String BIKE_LIGHT = "Sauce Labs Bike Light";

    @Test(description = "TC10 Add two items and remove one from the products page")
    public void TC10_addTwoItemsAndRemoveOneFromProductsPage() {
        ProductsPage products = loginAsStandardUser()
                .addToCart(BACKPACK)
                .addToCart(BIKE_LIGHT);

        // Step 3
        assertEquals(products.getCartBadgeCount(), 2, "Badge after adding two items");
        assertEquals(products.getCartButtonText(BACKPACK), "Remove");
        assertEquals(products.getCartButtonText(BIKE_LIGHT), "Remove");

        // Steps 4-5
        products.removeFromCart(BACKPACK);
        assertEquals(products.getCartBadgeCount(), 1, "Badge after removing the Backpack");
        assertEquals(products.getCartButtonText(BACKPACK), "Add to cart");

        // Step 6
        CartPage cart = products.openCart();
        assertEquals(cart.getItemNames(), List.of(BIKE_LIGHT), "Cart contents");
        assertEquals(cart.getItemQuantities(), List.of(1), "Quantity");
        assertEquals(cart.getItemPrices(), List.of(new BigDecimal("9.99")), "Price");
    }
}
