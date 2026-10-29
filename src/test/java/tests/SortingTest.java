package tests;

import base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.ProductsPage;
import utils.ConfigReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortingTest extends BaseClass {

    @Test(description = "TC07 Sort by price low to high")
    public void TC07_sortByPriceLowToHigh() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(ConfigReader.get("standardUser"), ConfigReader.get("password"));

        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.selectSortOption("Price (low to high)");

        List<Double> actualPrices = productsPage.getAllProductPrices();
        List<Double> expectedPrices = new ArrayList<>(actualPrices);
        Collections.sort(expectedPrices);

        Assert.assertEquals(actualPrices.size(), 6, "Number of products");
        Assert.assertEquals(actualPrices, expectedPrices, "Every price should be <= the one after it");
        Assert.assertEquals(productsPage.getSelectedSortOption(), "Price (low to high)");
    }

    @Test(description = "TC08 Sort by price high to low")
    public void TC08_sortByPriceHighToLow() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(ConfigReader.get("standardUser"), ConfigReader.get("password"));

        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.selectSortOption("Price (high to low)");

        List<Double> actualPrices = productsPage.getAllProductPrices();
        List<Double> expectedPrices = new ArrayList<>(actualPrices);
        expectedPrices.sort(Collections.reverseOrder());

        Assert.assertEquals(actualPrices.size(), 6, "Number of products");
        Assert.assertEquals(actualPrices, expectedPrices, "Every price should be >= the one after it");
        Assert.assertEquals(productsPage.getSelectedSortOption(), "Price (high to low)");
    }
}
