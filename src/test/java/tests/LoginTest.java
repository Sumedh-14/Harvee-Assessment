package tests;

import base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.ProductsPage;
import utils.ConfigReader;

public class LoginTest extends BaseClass {

    @Test(description = "TC01 Standard user logs in")
    public void TC01_standardUserLogsIn() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUserName(ConfigReader.get("standardUser"));
        loginPage.enterPassword(ConfigReader.get("password"));
        loginPage.clickLogin();

        ProductsPage productsPage = new ProductsPage(driver);
        Assert.assertTrue(productsPage.isProductsPageDisplayed(), "Products page should be displayed");
        Assert.assertEquals(productsPage.getPageTitle(), "Products");
    }

    @Test(description = "TC02 Locked-out user cannot log in")
    public void TC02_lockedOutUserCannotLogIn() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUserName(ConfigReader.get("lockedOutUser"));
        loginPage.enterPassword(ConfigReader.get("password"));
        loginPage.clickLogin();

        Assert.assertEquals(loginPage.getErrorMessage(), "Epic sadface: Sorry, this user has been locked out.");
        Assert.assertTrue(loginPage.isLoginPageDisplayed(), "User should stay on the login page");
    }
}
