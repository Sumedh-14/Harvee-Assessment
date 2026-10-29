# SauceDemo Automation Suite

Selenium 4 + Java + TestNG suite for https://www.saucedemo.com, built with the Page Object Model.

## Repository contents

| Path | What it is |
|------|------------|
| [`Manual_Test_Suite.md`](Manual_Test_Suite.md) / `Manual_Test_Suite.csv` | A1: the 26 manual test cases |
| `src/test/java/` | A2: page objects, tests and utilities |
| `testng.xml`, `pom.xml`, `src/test/resources/config.properties` | Suite, build and configuration |
| [`AI_USAGE.md`](AI_USAGE.md) | Where an AI coding assistant was used, and how its output was checked |

## Setup

Requirements: **JDK 17+**, **Maven 3.9+**, and Google Chrome (Firefox and Edge are also supported).
No driver download is needed: Selenium Manager fetches the matching driver automatically.

```bash
git clone <repo-url> harvee-assessment && cd harvee-assessment
mvn -q test-compile        # downloads dependencies and compiles
```

### Configuration

All environment-specific values live in `src/test/resources/config.properties`, not in the tests:

| Key              | Default                       | Env variable override |
|------------------|-------------------------------|-----------------------|
| `baseUrl`        | `https://www.saucedemo.com/`  | `BASE_URL`            |
| `standardUser`   | `standard_user`               | `STANDARD_USER`       |
| `lockedOutUser`  | `locked_out_user`             | `LOCKED_OUT_USER`     |
| `password`       | `secret_sauce`                | `PASSWORD`            |
| `browser`        | `chrome` (`firefox`, `edge`)  | `BROWSER`             |
| `headless`       | `false`                       | `HEADLESS`            |
| `timeoutSeconds` | `10`                          | `TIMEOUT_SECONDS`     |

Lookup order is `-Dkey=value` system property, then environment variable, then the properties file.

## Running

```bash
mvn test                                   # full suite (testng.xml), visible Chrome, 4 tests in parallel
mvn test -Dheadless=true                   # headless, e.g. on CI
mvn test -Dbrowser=firefox                 # another browser
mvn test -Dtest='CheckoutTest#TC17*'       # a single test on its own
```

### Reports and screenshots

| Output                                          | What it is                                  |
|-------------------------------------------------|---------------------------------------------|
| **`target/saucedemo-report.html`**              | **Main report**: one self-contained HTML file (summary, per-test status, errors, failure screenshots embedded); opens the same in any browser or IDE preview |
| `target/surefire-reports/emailable-report.html` | TestNG's built-in one-page summary          |
| `target/surefire-reports/index.html`            | Full TestNG HTML report, incl. failure logs |
| `target/surefire-reports/TEST-TestSuite.xml`    | JUnit XML for CI tools                      |
| `target/screenshots/*.png`                      | Screenshot of the browser for every failure; linked from the HTML report |

## Project structure

```
src/test/java/
├── base/BaseClass.java        fresh browser per test, screenshot on failure, shared login precondition
├── pages/                     page objects: locators + actions with explicit waits
│   ├── BasePage.java          header, burger menu, footer, wait helpers
│   ├── LoginPage.java
│   ├── ProductsPage.java
│   ├── CartPage.java
│   ├── CheckoutYourInformationPage.java
│   ├── CheckoutOverviewPage.java
│   └── CheckoutCompletePage.java
├── tests/                     test classes; no locators, no sleeps
│   ├── LoginTest.java         TC01, TC02
│   ├── SortingTest.java       TC07, TC08
│   ├── CartTest.java          TC10
│   ├── CheckoutTest.java      TC15, TC17
│   └── SecurityTest.java      TC20
└── utils/                     ConfigReader, DriverFactory, ScreenshotUtil, HtmlReportListener
src/test/resources/config.properties
testng.xml
```

Design rules the suite follows:

- **No locators in tests.** Tests call page methods only (`loginAs`, `sortBy`, `addToCart`, ...).
- **No fixed sleeps.** Every interaction goes through `WebDriverWait` (visibility, URL, invisibility, retrying clicks).
- **Independent tests.** Each test opens its own browser and logs in itself, so tests run alone, in any order, and in parallel.

## Manual to automated traceability

Manual cases are in the A1 suite: [`Manual_Test_Suite.md`](Manual_Test_Suite.md) (readable) and `Manual_Test_Suite.csv` (original sheet). Every automated test name starts with its manual case ID.
Cases marked "Yes" in A1 that are not automated yet are listed as *manual only (automation backlog)*.

| Manual case | Title                                                             | A1 Automate? | Automated test                                              |
|-------------|-------------------------------------------------------------------|--------------|-------------------------------------------------------------|
| TC01        | Standard user logs in                                             | Yes          | `LoginTest.TC01_standardUserLogsIn`                         |
| TC02        | Locked-out user cannot log in                                     | Yes          | `LoginTest.TC02_lockedOutUserCannotLogIn`                   |
| TC03        | Invalid user name or wrong password is rejected                   | Yes          | manual only (automation backlog)                            |
| TC04        | Empty username or password shows a required-field error           | Yes          | manual only (automation backlog)                            |
| TC05        | Products page lists all items with complete details               | Yes          | manual only (automation backlog)                            |
| TC06        | Sort by name A to Z and Z to A                                    | Yes          | manual only (automation backlog)                            |
| TC07        | Sort by price low to high                                         | Yes          | `SortingTest.TC07_sortByPriceLowToHigh`                     |
| TC08        | Sort by price high to low                                         | Yes          | `SortingTest.TC08_sortByPriceHighToLow`                     |
| TC09        | Back then Forward after login does not bypass login               | Yes          | manual only (automation backlog)                            |
| TC10        | Add two items and remove one from the products page               | Yes          | `CartTest.TC10_addTwoItemsAndRemoveOneFromProductsPage`     |
| TC11        | Remove an item from the cart page                                 | Yes          | manual only (automation backlog)                            |
| TC12        | Cart badge with zero items and with all items                     | Yes          | manual only (automation backlog)                            |
| TC13        | Cart is kept after logout and login as the same user              | No           | manual only (behaviour to be confirmed by the PO)           |
| TC14        | Cart of one user is not shown to another user                     | No           | manual only (explore first)                                 |
| TC15        | Required-field errors on checkout information (4 data rows)       | Yes          | `CheckoutTest.TC15_requiredFieldErrorsOnCheckoutInformation`|
| TC16        | Unusual values in the checkout fields                             | No           | manual only (validation rules pending PO)                   |
| TC17        | Full checkout: totals are correct and cart is empty after Finish  | Yes          | `CheckoutTest.TC17_fullCheckoutTotalsCorrectAndCartEmpty`   |
| TC18        | Cancel on the overview page does not place the order              | Yes          | manual only (automation backlog)                            |
| TC19        | Checkout with an empty cart is blocked                            | Yes          | manual only (rule to be confirmed first)                    |
| TC20        | Products page cannot be opened by URL after logout                | Yes          | `SecurityTest.TC20_productsPageCannotBeOpenedByUrlAfterLogout` |
| TC21        | Browser Back after logout does not reopen inner pages             | Yes          | manual only (automation backlog)                            |
| TC22        | Inner pages cannot be opened by URL without logging in            | Yes          | manual only (automation backlog)                            |
| TC23        | Error messages are clear and can be dismissed                     | No           | manual only (needs human judgement)                         |
| TC24        | Shop and check out on other browsers and screen sizes             | No           | manual only (future cross-browser CI run)                   |
| TC25        | Complete the purchase using only the keyboard                     | No           | manual only (axe scan as support)                           |
| TC26        | Reset App State clears the cart                                   | Yes          | manual only (automation backlog)                            |

## Growing to 200 tests

At 200 tests the structure stays the same, but a few things get promoted. Page objects split into reusable components (header, menu, product card, cart row), so a UI change is fixed in one place. Test data moves out of the test bodies into data providers backed by JSON/CSV files, and preconditions such as "logged in with two items in the cart" become builder-style helpers. Where the app allows it, they are set up through cookies or the API rather than the UI. Tests are tagged with TestNG groups (`smoke`, `regression`, per feature), so CI runs a fast smoke set on every pull request and the full regression nightly. Execution scales out through parallel runs on a Selenium Grid or a cloud provider, using the `ThreadLocal` driver already in place, with `DriverFactory` choosing a remote driver from config. Reporting moves to Allure or ExtentReports, with screenshots, browser logs and a retry analyzer that flags flaky tests instead of hiding them. Finally, a lint rule or code review rule keeps `By` locators and `Thread.sleep` out of the `tests` package.
