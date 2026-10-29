package tests;

import base.BaseClass;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.ProductsPage;
import utils.ConfigReader;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class LoginTest extends BaseClass {

    @Test(description = "TC01 Standard user logs in")
    public void TC01_standardUserLogsIn() {
        ProductsPage products = loginAsStandardUser();

        assertTrue(getDriver().getCurrentUrl().endsWith(ProductsPage.PATH), "URL after login");
        assertEquals(products.getPageTitle(), "Products");
    }

    @Test(description = "TC02 Locked-out user cannot log in")
    public void TC02_lockedOutUserCannotLogIn() {
        LoginPage login = openLoginPage()
                .loginExpectingError(ConfigReader.get("lockedOutUser"), ConfigReader.get("password"));

        assertEquals(login.getErrorMessage(), "Epic sadface: Sorry, this user has been locked out.");
        assertTrue(login.isDisplayed(), "User should stay on the login page");
    }
}
