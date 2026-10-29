package tests;

import base.BaseClass;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.ProductsPage;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class SecurityTest extends BaseClass {

    @Test(description = "TC20 Products page cannot be opened by URL after logout")
    public void TC20_productsPageCannotBeOpenedByUrlAfterLogout() {
        LoginPage login = loginAsStandardUser().logout();

        new ProductsPage(getDriver()).openByUrl();

        assertTrue(login.waitUntilLoaded().isDisplayed(), "Login page should be shown");
        assertEquals(login.getErrorMessage(),
                "Epic sadface: You can only access '/" + ProductsPage.PATH + "' when you are logged in.");
    }
}
