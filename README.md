# SauceDemo Automation Suite

Selenium 4 + Java + TestNG suite for https://www.saucedemo.com, built with the Page Object Model.

## Repository contents

| Path | What it is |
|------|------------|
| [`Manual_Test_Suite.md`](Manual_Test_Suite.md) / `Manual_Test_Suite.csv` | A1: the 26 manual test cases |
| `src/test/java/` | A2: page objects, tests and utilities |
| `testng.xml`, `pom.xml`, `src/test/resources/config.properties` | Suite, build and configuration |
| [`ANSWERS.md`](ANSWERS.md) | Section B answers: B1 AI test review, B2 AI feature evaluation, B3 defect report, B4 message to the PM |
| [`AI_USAGE.md`](AI_USAGE.md) | Where an AI coding assistant was used, and how its output was checked |

## Setup

Requirements: **JDK 17+**, **Maven 3.9+**, and Google Chrome (Firefox and Edge are also supported).
No driver download is needed: Selenium Manager fetches the matching driver automatically.

```bash
git clone https://github.com/Sumedh-14/Harvee-Assessment.git && cd Harvee-Assessment
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
mvn test                                   # full suite (testng.xml), visible Chrome, 4 test classes in parallel
mvn test -Dheadless=true                   # headless, e.g. on CI
mvn test -Dbrowser=firefox                 # another browser
mvn test -Dtest='CheckoutTest#TC17*'       # a single test on its own
```

### Reports and screenshots

| Output                                       | What it is |
|----------------------------------------------|------------|
| **`target/report/index.html`**               | **The report.** One self-contained HTML file: summary, per-test status by A1 ID, data rows, errors, and failure screenshots embedded. Open it with `open target/report/index.html`. |
| `target/surefire-reports/TEST-TestSuite.xml` | JUnit XML for CI tools |
| `target/screenshots/*.png`                   | Screenshot of the browser for every failure (also embedded in the report) |

**Latest report online:** https://sumedh-14.github.io/Harvee-Assessment/

The suite can be run on GitHub Actions by hand: open the **Actions** tab, choose **Tests**, then **Run workflow** (`.github/workflows/tests.yml`). It runs in headless Chrome and publishes the report to GitHub Pages, including runs where tests fail. The report, JUnit XML and screenshots are also attached to each run as the `test-report` artifact.

TestNG's built-in HTML reports are switched off in `pom.xml` (`usedefaultlisteners=false`). They depend on a retired Google script and render badly.

## Project structure

```
src/test/java/
├── base/BaseClass.java        driver: opens a fresh browser before each test, screenshot on failure, quits after
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

- **No locators in tests.** Locators are `private` in the page classes. Tests create page objects (`new LoginPage(driver)`) and call step methods such as `enterUserName(...)`, `clickLogin()`, `clickAddToCart(...)`.
- **No fixed sleeps.** Every interaction goes through `WebDriverWait` (visibility, URL, invisibility, retrying clicks).
- **Independent tests.** Each test opens its own browser and logs in itself, so tests run alone and in any order. `testng.xml` runs test classes in parallel (`parallel="classes"`); tests inside one class run one after another, because they share that class's `driver` field.

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

At 200 tests the basic structure stays the same: BaseClass, page classes and test classes. Screen parts that repeat on many pages, like the header, side menu and product card, get their own small classes, so a locator change is fixed in one place. Test data moves out of the test methods into JSON or CSV files that are read through TestNG data providers. Setup steps that many tests repeat, such as "log in and add two items to the cart", become one shared helper method. Tests are tagged with TestNG groups (`smoke`, `regression`), so a quick smoke run happens on every pull request and the full run happens nightly. To keep the run time short, the tests run on several browsers at once using Selenium Grid or a cloud service, and each test gets its own driver so tests can safely run in parallel. The report adds browser logs, and a failed test is retried once so that flaky tests are marked as flaky instead of hidden. Finally, code review keeps locators and `Thread.sleep` out of the test classes.
