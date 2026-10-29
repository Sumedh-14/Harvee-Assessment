# Section B – AI output and client communication

- [B1. Review of the AI-generated "cart total" test](#b1-review-of-the-ai-generated-cart-total-test)
- [B2. Evaluating the AI feature – Test case generator](#b2-evaluating-the-ai-feature--test-case-generator)
- [B3. Defect report](#b3-defect-report)
- [B4. Message to the product manager](#b4-message-to-the-product-manager)

## B1. Review of the AI-generated "cart total" test

### Problems found

1. **The assertion checks nothing (the main problem).** The label always contains text such as "Total: $32.39", so `toBeTruthy()` passes whenever the element exists. The test would still pass with a wrong total, a total of $0.00, or "Total: $NaN". It is named "cart total is correct" but never compares the total with the item prices or with item total + tax.
2. **A fixed sleep: `waitForTimeout(3000)`.** It adds 3 seconds to every run and is either too long (wasted time) or too short on a slow machine (flaky failures). Playwright already waits for elements before acting on them, so the sleep is not needed. If a wait is needed, it should be for a real condition, for example the button changing to "Remove".
3. **`"text=Add to cart"` does not say which product.** All 6 products have an "Add to cart" button. The old `page.click(selector)` API is not strict, so it quietly clicks the first match, which depends on the sort order and catalogue. The test does not know which item it added or what it costs, so it could not calculate the expected total.
4. **One item does not test a total.** With a single item, "item total = sum of prices" is trivially true. A total check needs at least two items with different prices to prove the prices are actually added up.
5. **No checks along the way.** It never checks that login worked, that the badge or cart has the item, or that each checkout page loaded. If something breaks early, the test fails much later with a confusing "element not found", or does not fail at all.
6. **URL and credentials are hard-coded.** `https://www.saucedemo.com`, `standard_user` and `secret_sauce` are written into the test. They belong in config (`baseURL` in `playwright.config.ts`) and environment variables; otherwise every test changes when the environment or password changes, and the password sits in the code.
7. **Fragile, mixed selectors written directly in the test.** It mixes ids (`#checkout`), styling classes (`.shopping_cart_link`, `.summary_total_label`) and text. Styling classes change when the design changes. The site provides `data-test` attributes for testing, and selectors should live in page objects, not in the test body.
8. **Money as text and floating point.** To check the total, "Total: $43.18" must be turned into a number. Adding prices as floating-point numbers can give 39.980000000000004, so compare in cents (whole numbers).
9. **Meaningless test data.** `'A'`, `'B'` and `'1'` pass today but hide intent and would break if postal code validation is added. Use realistic values from test data.
10. **Missing imports and no traceability.** `test` and `expect` are not imported, and the test name has no manual case ID linking it to the A1 suite (TC17).

### Rewritten test (Playwright, TypeScript)

Locators live in page objects, data comes from config, and all money comparisons are in cents.

```ts
// utils/money.ts
export const toCents = (text: string): number =>
  Math.round(parseFloat(text.replace(/[^0-9.]/g, '')) * 100);
```

```ts
// pages/InventoryPage.ts
import { Page, Locator } from '@playwright/test';
import { toCents } from '../utils/money';

export class InventoryPage {
  readonly cartBadge: Locator;
  private readonly cartLink: Locator;

  constructor(private readonly page: Page) {
    this.cartBadge = page.locator('[data-test="shopping-cart-badge"]');
    this.cartLink = page.locator('[data-test="shopping-cart-link"]');
  }

  private item(name: string): Locator {
    return this.page.locator('[data-test="inventory-item"]', { hasText: name });
  }

  async priceInCents(name: string): Promise<number> {
    const text = await this.item(name).locator('[data-test="inventory-item-price"]').innerText();
    return toCents(text);
  }

  async addToCart(name: string): Promise<void> {
    await this.item(name).getByRole('button', { name: 'Add to cart' }).click();
  }

  async openCart(): Promise<void> {
    await this.cartLink.click();
  }
}
```

```ts
// pages/CheckoutOverviewPage.ts
import { Page, Locator } from '@playwright/test';
import { toCents } from '../utils/money';

export class CheckoutOverviewPage {
  readonly itemTotal: Locator;
  readonly tax: Locator;
  readonly total: Locator;

  constructor(page: Page) {
    this.itemTotal = page.locator('[data-test="subtotal-label"]');
    this.tax = page.locator('[data-test="tax-label"]');
    this.total = page.locator('[data-test="total-label"]');
  }

  async amountsInCents() {
    return {
      itemTotal: toCents(await this.itemTotal.innerText()),
      tax: toCents(await this.tax.innerText()),
      total: toCents(await this.total.innerText()),
    };
  }
}
```

`LoginPage`, `CartPage` and `CheckoutInfoPage` follow the same pattern and are left out here.

```ts
// tests/checkout.spec.ts
import { test, expect } from '@playwright/test';
import { users, checkoutCustomer } from '../config/testData';
import { LoginPage } from '../pages/LoginPage';
import { InventoryPage } from '../pages/InventoryPage';
import { CartPage } from '../pages/CartPage';
import { CheckoutInfoPage } from '../pages/CheckoutInfoPage';
import { CheckoutOverviewPage } from '../pages/CheckoutOverviewPage';

const PRODUCTS = ['Sauce Labs Backpack', 'Sauce Labs Bike Light'];

test('TC17 overview total equals item total plus tax', async ({ page }) => {
  const login = new LoginPage(page);
  const inventory = new InventoryPage(page);
  const cart = new CartPage(page);
  const info = new CheckoutInfoPage(page);
  const overview = new CheckoutOverviewPage(page);

  await login.open();                                   // uses baseURL from config
  await login.loginAs(users.standard.username, users.standard.password);
  await expect(page).toHaveURL(/inventory\.html/);

  let expectedItemTotal = 0;
  for (const name of PRODUCTS) {
    expectedItemTotal += await inventory.priceInCents(name);
    await inventory.addToCart(name);
  }
  await expect(inventory.cartBadge).toHaveText(String(PRODUCTS.length));

  await inventory.openCart();
  await expect(cart.items).toHaveCount(PRODUCTS.length);
  await cart.checkout();

  await info.submit(checkoutCustomer);                  // John / Doe / 560001
  await expect(page).toHaveURL(/checkout-step-two\.html/);

  const { itemTotal, tax, total } = await overview.amountsInCents();
  expect(itemTotal).toBe(expectedItemTotal);            // item total = sum of product prices
  expect(tax).toBeGreaterThan(0);                       // tax is applied
  expect(total).toBe(itemTotal + tax);                  // total = item total + tax
});
```

### What changed

- The assertions now check the business rule: item total = sum of product prices, and total = item total + tax.
- Products are chosen by name, so the expected values are known.
- The fixed sleep is gone; Playwright's built-in waits and web-first assertions handle timing.
- Config replaces hard-coded data, and locators sit in page objects.
- Checks along the way make any failure point to the right step.
- The test name carries the manual case ID (TC17).
- The 8% tax rate is not hard-coded because the business rules do not state it. I would confirm the rate with the product owner and then also assert the tax amount.

## B2. Evaluating the AI feature – Test case generator

**Chosen scenario:** an internal tool turns a user story into test cases in a fixed JSON format, which is imported straight into the test management tool.

**Evaluation set:** about 20 real user stories from past sprints, each with its requirement document. The set is re-run on every prompt or model change, like a regression suite.

| # | Check | What it catches | Scoring method | How it is scored |
|---|---|---|---|---|
| 1 | No duplicate cases | The same test case generated twice, which inflates the suite and wastes execution time | Automatically | A script compares the steps, test data and expected result of every case in the output. Any two identical cases count as a duplicate; any duplicate is a fail. |
| 2 | No cases outside the requirement | Cases that test rules the requirement never states, e.g. "password must be 8 characters" when nothing is said about length. These import cleanly and look like real requirements. | By human review | A QA engineer who knows the product reads every case against the requirement document and marks it "from the requirement" or "invented". Any invented case is a fail. |
| 3 | Test coverage | Requirements with no test case | Automatically | Assumption: every requirement in the document has an ID, and every generated case carries the ID it tests. A script lists the requirement IDs that no case refers to. Any uncovered requirement is a fail. |
| 4 | Output matches the required format | Broken JSON, missing required fields, wrong field types or values, extra text around the JSON. Any of these breaks the import. | Automatically | Validate every output against the JSON schema the test management tool uses. Every output must pass. |

**Which mistake costs more:** broken JSON is visible straight away, because the import fails. Invented requirements are worse: they import cleanly, testers run them, and false defects get raised against behaviour nobody asked for. That is why check 2 is scored by a human even though it is the slowest check to run.

## B3. Defect report

**Title:** [Sorting] "Price (low to high)" shows a $29.99 item before a $15.99 item – regression in 4.2.0

| Field | Value |
|---|---|
| Project / Component | Sauce Demo – Products page / Sorting |
| Issue type | Bug |
| Affects version | 4.2.0 |
| Severity | Major (S2) |
| Priority | High |
| Labels | regression, sorting, products-page |
| Linked test case | TC07 – Sort by price low to high (failing since 4.2.0) |
| Reproducibility | Always – every run since 4.2.0 |

### Environment

- Build / release: 4.2.0
- Application URL: Sauce Demo test environment (base URL as in the test config)
- Browser: Google Chrome, version 156.0.8078.48
- OS: macOS 26.5.2
- User: standard_user
- Screen size: 1920×1080 (desktop)

### Pre-conditions

User is logged in as standard_user and is on the Products page.

### Steps to reproduce

1. Open the application and log in as standard_user / secret_sauce.
2. On the Products page, open the sort dropdown.
3. Select "Price (low to high)".
4. Read the prices of all products from top to bottom.

### Expected result

All products are listed in ascending order of price; every price is less than or equal to the one below it (e.g. $7.99 → $9.99 → $15.99 → $15.99 → $29.99 → $49.99), as per the business rule "Products can be sorted by price (low to high)".

### Actual result

The list is not in ascending order: an item priced $29.99 is shown above an item priced $15.99. The rest of the order is as captured in the attached screenshot. The sort selection itself is accepted (the dropdown shows "Price (low to high)").

### Severity – reason

Major (S2), not Critical:

- It breaks a stated business rule and affects every user who sorts by price, on every run.
- Customers looking for the cheapest products are shown a wrong order, which can mislead them and lose sales.
- It is not a blocker: login, cart and checkout still work, and users can still find and buy any product by scrolling.

Priority High: it is a regression introduced in 4.2.0 and sits on the main shopping path, so it should be fixed before 4.2.0 goes to production.

### Additional notes

- Regression: sorting by price worked in the previous release; the automated TC07 test started failing with 4.2.0.
- Still to check (will update this ticket): whether "Price (high to low)" is also affected, whether it appears on Firefox / Edge or for other users, and whether list prices match the product detail pages (to rule out a data problem).

### Evidence attached

- Screenshot of the Products page after selecting "Price (low to high)", with the $29.99 and $15.99 items highlighted.
- Short screen recording (10–20 s) showing the steps from login to the wrong order.
- Automation report for TC07 (HTML report and failure screenshot) showing the assertion failure with the full list of prices.
- Browser console and network logs (HAR file) captured during the sort.
- Comparison – screenshot or test report from the previous release showing the correct order.

## B4. Message to the product manager

**Channel:** Email
**Subject:** Friday release – one fix needed

> Hi &lt;name&gt;,
>
> This mail is a quick update regarding the Friday release.
>
> Price sorting is wrong. When customers sort by "Price (low to high)", a $29.99 item appears above a $15.99 one, every time.
>
> Checkout works, but one of our checkout checks fails about 1 time in 10. It may be our test rather than the site; we are confirming that today.
>
> My recommendation: we fix the sorting today, I retest on Friday morning, and we release on Friday afternoon.
>
> If the fix can't make it, we can release on Friday as planned with the price sort options hidden, and bring them back in a small update next week.
>
> Either way, I will check the checkout flow before the release and watch it closely once it is live.
>
> Could you let me know which option you prefer by 5 pm today?
>
> Thanks,
> Sumedh
