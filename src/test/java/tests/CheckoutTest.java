package tests;

import base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CheckoutCompletePage;
import pages.CheckoutOverviewPage;
import pages.CheckoutYourInformationPage;
import pages.LoginPage;
import pages.ProductsPage;
import utils.ConfigReader;

import java.util.List;

public class CheckoutTest extends BaseClass {

    private static final String BACKPACK = "Sauce Labs Backpack";
    private static final String BIKE_LIGHT = "Sauce Labs Bike Light";

    @DataProvider(name = "missingCheckoutInfo")
    public Object[][] missingCheckoutInfo() {
        return new Object[][]{
                // first name, last name, postal code, expected error
                {"",     "User", "560001", "Error: First Name is required"},
                {"Test", "",     "560001", "Error: Last Name is required"},
                {"Test", "User", "",       "Error: Postal Code is required"},
                {"",     "",     "",       "Error: First Name is required"},
        };
    }

    @Test(dataProvider = "missingCheckoutInfo", description = "TC15 Required-field errors on checkout information")
    public void TC15_requiredFieldErrorsOnCheckoutInformation(String firstName, String lastName, String postalCode,
                                                              String expectedError) {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(ConfigReader.get("standardUser"), ConfigReader.get("password"));

        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.clickAddToCart(BACKPACK);
        productsPage.clickCartIcon();

        CartPage cartPage = new CartPage(driver);
        cartPage.clickCheckout();

        CheckoutYourInformationPage informationPage = new CheckoutYourInformationPage(driver);
        informationPage.enterFirstName(firstName);
        informationPage.enterLastName(lastName);
        informationPage.enterPostalCode(postalCode);
        informationPage.clickContinue();

        Assert.assertEquals(informationPage.getErrorMessage(), expectedError);
        Assert.assertTrue(informationPage.isCheckoutInformationPageDisplayed(), "User should stay on the information page");
    }

    @Test(description = "TC17 Full checkout: totals are correct and cart is empty after Finish")
    public void TC17_fullCheckoutTotalsCorrectAndCartEmpty() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(ConfigReader.get("standardUser"), ConfigReader.get("password"));

        // Steps 1-2: add two items, open cart, click Checkout
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.clickAddToCart(BACKPACK);
        productsPage.clickAddToCart(BIKE_LIGHT);
        productsPage.clickCartIcon();

        CartPage cartPage = new CartPage(driver);
        cartPage.clickCheckout();

        // Step 3: enter details and continue
        CheckoutYourInformationPage informationPage = new CheckoutYourInformationPage(driver);
        informationPage.enterFirstName("Test");
        informationPage.enterLastName("User");
        informationPage.enterPostalCode("560001");
        informationPage.clickContinue();

        // Step 4: check the overview page
        CheckoutOverviewPage overviewPage = new CheckoutOverviewPage(driver);
        Assert.assertTrue(overviewPage.isCheckoutOverviewPageDisplayed(), "Overview page should be displayed");
        Assert.assertEquals(overviewPage.getItemNames(), List.of(BACKPACK, BIKE_LIGHT), "Items in the order");

        double sumOfPrices = 0;
        for (double price : overviewPage.getItemPrices()) {
            sumOfPrices += price;
        }
        double itemTotal = overviewPage.getItemTotal();
        double tax = overviewPage.getTax();
        double total = overviewPage.getTotal();

        Assert.assertEquals(itemTotal, sumOfPrices, 0.001, "Item total = sum of item prices");
        Assert.assertEquals(total, itemTotal + tax, 0.001, "Total = item total + tax");

        // Steps 5-6: finish, check confirmation, badge and cart
        overviewPage.clickFinish();

        CheckoutCompletePage completePage = new CheckoutCompletePage(driver);
        Assert.assertTrue(completePage.isCheckoutCompletePageDisplayed(), "Complete page should be displayed");
        Assert.assertEquals(completePage.getConfirmationMessage(), "Thank you for your order!");
        Assert.assertTrue(completePage.isBackHomeButtonDisplayed(), "Back Home button should be displayed");
        Assert.assertEquals(completePage.getCartBadgeCount(), 0, "No badge after checkout");

        completePage.clickCartIcon();
        Assert.assertTrue(cartPage.isCartEmpty(), "Cart should be empty after checkout");
    }
}
