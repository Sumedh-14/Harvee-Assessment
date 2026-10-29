package tests;

import base.BaseClass;
import org.testng.annotations.Test;
import pages.ProductsPage;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class SortingTest extends BaseClass {

    @Test(description = "TC07 Sort by price low to high")
    public void TC07_sortByPriceLowToHigh() {
        ProductsPage products = loginAsStandardUser().sortBy(ProductsPage.SORT_PRICE_LOW_HIGH);

        List<BigDecimal> prices = products.getProductPrices();
        assertEquals(prices.size(), 6, "Number of products");
        assertEquals(prices, prices.stream().sorted().toList(), "Each price should be <= the one after it");
        assertEquals(products.getActiveSortOption(), "Price (low to high)");
    }

    @Test(description = "TC08 Sort by price high to low")
    public void TC08_sortByPriceHighToLow() {
        ProductsPage products = loginAsStandardUser().sortBy(ProductsPage.SORT_PRICE_HIGH_LOW);

        List<BigDecimal> prices = products.getProductPrices();
        assertEquals(prices.size(), 6, "Number of products");
        assertEquals(prices, prices.stream().sorted(Comparator.reverseOrder()).toList(), "Each price should be >= the one after it");
        assertEquals(products.getActiveSortOption(), "Price (high to low)");
    }
}
