package tests;

import base.BaseClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CheckoutCompletePage;
import pages.CheckoutOverviewPage;
import pages.CheckoutYourInformationPage;

import java.math.BigDecimal;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class CheckoutTest extends BaseClass {

    private static final String BACKPACK = "Sauce Labs Backpack";
    private static final String BIKE_LIGHT = "Sauce Labs Bike Light";

    @DataProvider(name = "missingCheckoutInfo")
    public Object[][] missingCheckoutInfo() {
        return new Object[][]{
                {"",     "User", "560001", "Error: First Name is required"},
                {"Test", "",     "560001", "Error: Last Name is required"},
                {"Test", "User", "",       "Error: Postal Code is required"},
                {"",     "",     "",       "Error: First Name is required"},
        };
    }

    @Test(dataProvider = "missingCheckoutInfo", description = "TC15 Required-field errors on checkout information")
    public void TC15_requiredFieldErrorsOnCheckoutInformation(String firstName, String lastName, String postalCode,
                                                              String expectedError) {
        CheckoutYourInformationPage info = loginAsStandardUser()
                .addToCart(BACKPACK)
                .openCart()
                .checkout()
                .fillInformation(firstName, lastName, postalCode)
                .continueExpectingError();

        assertEquals(info.getErrorMessage(), expectedError);
        assertTrue(info.isDisplayed(), "User should stay on the information page");
    }

    @Test(description = "TC17 Full checkout: totals are correct and cart is empty after Finish")
    public void TC17_fullCheckoutTotalsCorrectAndCartEmpty() {
        CheckoutOverviewPage overview = loginAsStandardUser()
                .addToCart(BACKPACK)
                .addToCart(BIKE_LIGHT)
                .openCart()
                .checkout()
                .fillInformation("Test", "User", "560001")
                .continueToOverview();

        // Step 4
        assertEquals(overview.getItemNames(), List.of(BACKPACK, BIKE_LIGHT), "Items in the order");
        BigDecimal sumOfPrices = overview.getItemPrices().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(overview.getItemTotal(), sumOfPrices, "Item total = sum of item prices");
        assertEquals(overview.getTotal(), overview.getItemTotal().add(overview.getTax()), "Total = item total + tax");

        // Steps 5-6
        CheckoutCompletePage complete = overview.finish();
        assertEquals(complete.getConfirmationHeader(), "Thank you for your order!");
        assertTrue(complete.isBackHomeButtonDisplayed(), "Back Home button should be shown");
        assertEquals(complete.getCartBadgeCount(), 0, "No badge after checkout");

        CartPage cart = complete.openCart();
        assertTrue(cart.isEmpty(), "Cart should be empty after checkout");
    }
}
