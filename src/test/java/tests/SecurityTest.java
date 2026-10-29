package tests;

import base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.ProductsPage;
import utils.ConfigReader;

public class SecurityTest extends BaseClass {

    @Test(description = "TC20 Products page cannot be opened by URL after logout")
    public void TC20_productsPageCannotBeOpenedByUrlAfterLogout() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(ConfigReader.get("standardUser"), ConfigReader.get("password"));

        // Step 1: logout from the menu
        ProductsPage productsPage = new ProductsPage(driver);
        Assert.assertTrue(productsPage.isProductsPageDisplayed(), "Products page should be displayed after login");
        productsPage.logout();
        Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Login page should be displayed after logout");

        // Step 2: type base URL + /inventory.html in the address bar
        driver.get(ConfigReader.get("baseUrl") + "inventory.html");

        Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Login page should be displayed");
        Assert.assertEquals(loginPage.getErrorMessage(),
                "Epic sadface: You can only access '/inventory.html' when you are logged in.");
    }
}
