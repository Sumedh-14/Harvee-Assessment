# SauceDemo – Manual Test Suite (A1)

Application under test: https://www.saucedemo.com/  
Password for all users: `secret_sauce`

**26 test cases** · 20 marked for automation · 8 automated so far

Priority: **P1** = must pass for release (smoke), **P2** = important, **P3** = nice to have.

## Summary

| ID | Area | Type | Title | Priority | Automate? | Status |
|----|------|------|-------|----------|-----------|--------|
| TC01 | Login | Positive | Standard user logs in | P1 | Yes | Automated – `LoginTest.TC01_standardUserLogsIn` |
| TC02 | Login | Negative | Locked-out user cannot log in | P1 | Yes | Automated – `LoginTest.TC02_lockedOutUserCannotLogIn` |
| TC03 | Login | Negative | Invalid User Name or wrong password is rejected | P1 | Yes | Manual only (automation backlog) |
| TC04 | Login | Negative | Empty username or password shows a required-field error | P2 | Yes | Manual only (automation backlog) |
| TC05 | Product List | Positive | Products page lists all items with complete details | P2 | Yes | Manual only (automation backlog) |
| TC06 | Sorting | Positive | Sort by name A to Z and Z to A | P2 | Yes | Manual only (automation backlog) |
| TC07 | Sorting | Positive | Sort by price low to high | P1 | Yes | Automated – `SortingTest.TC07_sortByPriceLowToHigh` |
| TC08 | Sorting | Positive | Sort by price high to low | P1 | Yes | Automated – `SortingTest.TC08_sortByPriceHighToLow` |
| TC09 | Product List | Negative | Try to login without creds by just going back and proceed forward | P2 | Yes | Manual only (automation backlog) |
| TC10 | Cart | Positive | Add two items and remove one from the products page | P1 | Yes | Automated – `CartTest.TC10_addTwoItemsAndRemoveOneFromProductsPage` |
| TC11 | Cart | Positive | Remove an item from the cart page | P2 | Yes | Manual only (automation backlog) |
| TC12 | Cart | Boundary | Cart badge with zero items and with all items | P2 | Yes | Manual only (automation backlog) |
| TC13 | Cart | Positive | Cart is kept after logout and login as the same user | P3 | No | Manual only |
| TC14 | Cart | Negative | Cart of one user is not shown to another user | P2 | No | Manual only |
| TC15 | Checkout | Negative | Required-field errors on checkout information | P1 | Yes | Automated – `CheckoutTest.TC15_requiredFieldErrorsOnCheckoutInformation` |
| TC16 | Checkout | Boundary | Unusual values in the checkout fields | P3 | No | Manual only |
| TC17 | Checkout | Positive | Full checkout: totals are correct and cart is empty after Finish | P1 | Yes | Automated – `CheckoutTest.TC17_fullCheckoutTotalsCorrectAndCartEmpty` |
| TC18 | Checkout | Negative | Cancel on the overview page does not place the order | P2 | Yes | Manual only (automation backlog) |
| TC19 | Checkout | Negative | Checkout with an empty cart is blocked | P2 | Yes | Manual only (automation backlog) |
| TC20 | Security | Negative | Products page cannot be opened by URL after logout | P1 | Yes | Automated – `SecurityTest.TC20_productsPageCannotBeOpenedByUrlAfterLogout` |
| TC21 | Security | Negative | Browser Back after logout does not reopen inner pages | P2 | Yes | Manual only (automation backlog) |
| TC22 | Security | Negative | Inner pages cannot be opened by URL without logging in | P2 | Yes | Manual only (automation backlog) |
| TC23 | Login | Usability | Error messages are clear and can be dismissed | P3 | No | Manual only |
| TC24 | Cross-browser | Browser / screen size | Shop and check out on other browsers and screen sizes | P2 | No | Manual only |
| TC25 | Accessibility | Accessibility | Complete the purchase using only the keyboard | P2 | No | Manual only |
| TC26 | Menu | Positive | Reset App State clears the cart | P3 | Yes | Manual only (automation backlog) |

## Test cases

### TC01 – Standard user logs in

| Field | Details |
|-------|---------|
| Area | Login |
| Type | Positive |
| Priority | P1 |
| Automate? | Yes – smoke test on every build |
| Status | Automated – `LoginTest.TC01_standardUserLogsIn` |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"

**Steps**

1. Enter valid "User name"
2. Enter valid "Password"
3. Click "Login"

**Test data**

standard_user / secret_sauce

**Expected result**

The user should be able to login successfully and the products page should display

---

### TC02 – Locked-out user cannot log in

| Field | Details |
|-------|---------|
| Area | Login |
| Type | Negative |
| Priority | P1 |
| Automate? | Yes |
| Status | Automated – `LoginTest.TC02_lockedOutUserCannotLogIn` |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"

**Steps**

1. Enter the locked-out user name
2. Enter valid "Password"
3. Click "Login"

**Test data**

locked_user / secret_sauce

**Expected result**

User stays on the login page and the error "Epic sadface: Sorry, this user has been locked out." should be thrown

---

### TC03 – Invalid User Name or wrong password is rejected

| Field | Details |
|-------|---------|
| Area | Login |
| Type | Negative |
| Priority | P1 |
| Automate? | Yes – smoke test on every build |
| Status | Manual only (automation backlog) |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"

**Steps**

1. Enter username
2. Enter password
3. Click "Login"

**Test data**

- a. abc_test
- b. standard_user / wrong_pass

**Expected result**

The user should not be able to login and an error message should be thrown

---

### TC04 – Empty username or password shows a required-field error

| Field | Details |
|-------|---------|
| Area | Login |
| Type | Negative |
| Priority | P2 |
| Automate? | Yes – smoke test on every build |
| Status | Manual only (automation backlog) |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"

**Steps**

1. Leave the field empty as per the data row
2. Click "Login"

**Test data**

- a) (empty) / secret_sauce
- b) standard_user / (empty)
- c) (empty) / (empty)

**Expected result**

- a) "Epic sadface: Username is required"
- b) "Epic sadface: Password is required"
- c) "Epic sadface: Username is required"
- User is not logged in for any row

---

### TC05 – Products page lists all items with complete details

| Field | Details |
|-------|---------|
| Area | Product List |
| Type | Positive |
| Priority | P2 |
| Automate? | Yes – catches broken or missing data |
| Status | Manual only (automation backlog) |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"
2. Login with valid user credential

**Steps**

1. Observe the product list

**Test data**

- standard_user
- Valid password

**Expected result**

User should be launched to product list screen and all the products should display along with the respective images, names, description, price in $X.XX format and an "Add to cart" button; no cart badge should display

---

### TC06 – Sort by name A to Z and Z to A

| Field | Details |
|-------|---------|
| Area | Sorting |
| Type | Positive |
| Priority | P2 |
| Automate? | Yes – assertion on the whole list, independent of catalogue content |
| Status | Manual only (automation backlog) |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"
2. Login with valid user credential

**Steps**

1. Note the default order
2. Select "Name (Z to A)"
3. Select "Name (A to Z)"

**Test data**

Sort options

**Expected result**

- Default order should be A to Z.
- After step 2 the whole list should be in reverse alphabetical order;
- After step 3 the whole list should be in alphabetical order

---

### TC07 – Sort by price low to high

| Field | Details |
|-------|---------|
| Area | Sorting |
| Type | Positive |
| Priority | P1 |
| Automate? | Yes |
| Status | Automated – `SortingTest.TC07_sortByPriceLowToHigh` |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"
2. Login with valid user credential

**Steps**

1. Select "Price (low to high)"
2. Read all 6 prices from top to bottom

**Test data**

Sort option "Price (low to high)"

**Expected result**

Every price is less than or equal to the one after it (currently 7.99, 9.99, 15.99, 15.99, 29.99, 49.99)

---

### TC08 – Sort by price high to low

| Field | Details |
|-------|---------|
| Area | Sorting |
| Type | Positive |
| Priority | P1 |
| Automate? | Yes |
| Status | Automated – `SortingTest.TC08_sortByPriceHighToLow` |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"
2. Login with valid user credential

**Steps**

1. Select "Price (high to low)"
2. Read all 6 prices from top to bottom

**Test data**

Sort option "Price (high to low)"

**Expected result**

Every price is greater than or equal to the one after it (currently 49.99 down to 7.99)

---

### TC09 – Try to login without creds by just going back and proceed forward

| Field | Details |
|-------|---------|
| Area | Product List |
| Type | Negative |
| Priority | P2 |
| Automate? | Yes |
| Status | Manual only (automation backlog) |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"
2. Login with valid user credential

**Steps**

1. From  product list page, tap on the browser back button.
2. Tap on the browser forward button

**Test data**

–

**Expected result**

The user should not be allowed to login again when the user tries to go forward from login screen without providing valid username and password.

---

### TC10 – Add two items and remove one from the products page

| Field | Details |
|-------|---------|
| Area | Cart |
| Type | Positive |
| Priority | P1 |
| Automate? | Yes |
| Status | Automated – `CartTest.TC10_addTwoItemsAndRemoveOneFromProductsPage` |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"
2. Login with valid user credential

**Steps**

1. Click "Add to cart" on Sauce Labs Backpack
2. Click "Add to cart" on Sauce Labs Bike Light
3. Check the cart badge
4. Click "Remove" on Sauce Labs Backpack
5. Check the cart badge
6. Open the cart

**Test data**

Sauce Labs Backpack, Sauce Labs Bike Light

**Expected result**

- Step 3: badge shows "2" and both buttons show "Remove".
- Step 5: badge shows "1" and the Backpack button shows "Add to cart" again.
- Step 6: cart lists only Sauce Labs Bike Light, quantity 1, $9.99

---

### TC11 – Remove an item from the cart page

| Field | Details |
|-------|---------|
| Area | Cart |
| Type | Positive |
| Priority | P2 |
| Automate? | Yes |
| Status | Manual only (automation backlog) |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"
2. Login with valid user credential
3. Sauce Labs Backpack and Sauce Labs Bike Light in cart

**Steps**

1. Open the cart
2. Click "Remove" on Sauce Labs Backpack
3. Click "Continue Shopping"

**Test data**

Sauce Labs Backpack, Sauce Labs Bike Light

**Expected result**

Backpack should disappear from the cart; badge should show "1"; on the products page the Backpack button should show "Add to cart"

---

### TC12 – Cart badge with zero items and with all items

| Field | Details |
|-------|---------|
| Area | Cart |
| Type | Boundary |
| Priority | P2 |
| Automate? | Yes – boundary values for the badge count |
| Status | Manual only (automation backlog) |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"
2. Login with valid user credential
3. Empty cart

**Steps**

1. Check the cart badge
2. Add all 6 products
3. Check the cart badge
4. Remove all 6 products
5. Check the cart badge

**Test data**

All 6 products

**Expected result**

- Steps 1 and 5: no badge should be shown (not "0").
- Step 3: badge should show "6"

---

### TC13 – Cart is kept after logout and login as the same user

| Field | Details |
|-------|---------|
| Area | Cart |
| Type | Positive |
| Priority | P3 |
| Automate? | No – behaviour not confirmed; automate once the PO confirms it |
| Status | Manual only |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"
2. Login with valid user credential
3. Sauce Labs Backpack in cart

**Steps**

1. Open the menu and click "Logout"
2. Log in again as the same user
3. Check the badge and the cart

**Test data**

standard_user / secret_sauce

**Expected result**

- Backpack is still in the cart and the badge shows "1".
- Assumption: the cart is saved per user. To be confirmed with the PO

---

### TC14 – Cart of one user is not shown to another user

| Field | Details |
|-------|---------|
| Area | Cart |
| Type | Negative |
| Priority | P2 |
| Automate? | No – explore manually first; if it is a defect, add it as an automated regression test |
| Status | Manual only |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"
2. Login with valid user credential
3. Sauce Labs Backpack in cart

**Steps**

1. Open the menu and click "Logout"
2. In the same browser, log in as problem_user
3. Check the badge and the cart

**Test data**

- First user: standard_user
- Second user: problem_user

**Expected result**

- problem_user's cart is empty and no badge is shown.
- Assumption: carts belong to the user, not the browser

---

### TC15 – Required-field errors on checkout information

| Field | Details |
|-------|---------|
| Area | Checkout |
| Type | Negative |
| Priority | P1 |
| Automate? | Yes – one parameterized test; required in A2 |
| Status | Automated – `CheckoutTest.TC15_requiredFieldErrorsOnCheckoutInformation` |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"
2. Login with valid user credential
3. one item in cart
4. On the "Checkout: Your Information" page

**Steps**

1. Fill the fields as per the data row
2. Click "Continue"

**Test data**

- a) (empty) / User / 560001
- b) Test / (empty) / 560001
- c) Test / User / (empty)
- d) all fields empty

**Expected result**

- a) "Error: First Name is required"
- b) "Error: Last Name is required"
- c) "Error: Postal Code is required"
- d) "Error: First Name is required"
- User stays on the information page for every row

---

### TC16 – Unusual values in the checkout fields

| Field | Details |
|-------|---------|
| Area | Checkout |
| Type | Boundary |
| Priority | P3 |
| Automate? | No – depends on PO answers about validation rules |
| Status | Manual only |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"
2. Login with valid user credential
3. one item in cart
4. On the "Checkout: Your Information" page

**Steps**

1. Enter the values as per the data row
2. Click "Continue"

**Test data**

- a) spaces only in all fields
- b) 255-character first and last name
- c) O'Brien-Ä as the last name
- d) ABCDE as the postal code

**Expected result**

- a) An error is shown; spaces are not accepted as a name.
- b) Accepted without breaking the layout, or rejected with a clear message.
- c) Accepted.
- d) As per the PO's answer on postal code format.
- Assumption: no postal code format check; spaces-only input should be rejected

---

### TC17 – Full checkout: totals are correct and cart is empty after Finish

| Field | Details |
|-------|---------|
| Area | Checkout |
| Type | Positive |
| Priority | P1 |
| Automate? | Yes – revenue-critical path; required in A2 |
| Status | Automated – `CheckoutTest.TC17_fullCheckoutTotalsCorrectAndCartEmpty` |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"
2. Login with valid user credential
3. Empty cart

**Steps**

1. Add Sauce Labs Backpack and Sauce Labs Bike Light
2. Open the cart and click "Checkout"
3. Enter first name, last name, postal code and click "Continue"
4. Check the "Checkout: Overview" page
5. Click "Finish"
6. Check the page, the badge and the cart

**Test data**

- Test / User / 560001
- Backpack $29.99, Bike Light $9.99

**Expected result**

- Step 4: both items are listed; Item total equals the sum of item prices ($39.98); Total equals Item total + Tax (tax currently 8%: $3.20, total $43.18).
- Step 6: "Thank you for your order!" is shown with a "Back Home" button; no badge is shown; the cart is empty

---

### TC18 – Cancel on the overview page does not place the order

| Field | Details |
|-------|---------|
| Area | Checkout |
| Type | Negative |
| Priority | P2 |
| Automate? | Yes – business rule for Cancel |
| Status | Manual only (automation backlog) |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"
2. Login with valid user credential
3. Sauce Labs Backpack in cart
4. On the "Checkout: Overview" page

**Steps**

1. Click "Cancel"
2. Check the page, the badge and the cart

**Test data**

–

**Expected result**

User returns to the products page; no order confirmation is shown; badge shows "1"; Backpack is still in the cart

---

### TC19 – Checkout with an empty cart is blocked

| Field | Details |
|-------|---------|
| Area | Checkout |
| Type | Negative |
| Priority | P2 |
| Automate? | Yes – regression check once the rule is confirmed |
| Status | Manual only (automation backlog) |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"
2. Login with valid user credential
3. Empty cart

**Steps**

1. Open the cart
2. Click "Checkout"
3. Enter valid details and click "Continue"
4. Click "Finish"

**Test data**

John / Doe / 560001

**Expected result**

- The "Checkout" button is disabled or an error is shown; no order is placed.
- Assumption: an order with no items is not allowed. If checkout goes through, report it as a defect

---

### TC20 – Products page cannot be opened by URL after logout

| Field | Details |
|-------|---------|
| Area | Security |
| Type | Negative |
| Priority | P1 |
| Automate? | Yes – access-control rule; required in A2 |
| Status | Automated – `SecurityTest.TC20_productsPageCannotBeOpenedByUrlAfterLogout` |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"
2. Login with valid user credential

**Steps**

1. Open the menu and click "Logout"
2. Type base URL + /inventory.html in the address bar

**Test data**

/inventory.html

**Expected result**

Login page is shown with the error "Epic sadface: You can only access '/inventory.html' when you are logged in."

---

### TC21 – Browser Back after logout does not reopen inner pages

| Field | Details |
|-------|---------|
| Area | Security |
| Type | Negative |
| Priority | P2 |
| Automate? | Yes – same rule as TC19 through a different path |
| Status | Manual only (automation backlog) |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"
2. Login with valid user credential
3. On the products page

**Steps**

1. Open the menu and click "Logout"
2. Click the browser Back button
3. If a products page appears, click "Add to cart" on any item

**Test data**

–

**Expected result**

Login page is shown (or the user is redirected to it); no inner page can be used without logging in again

---

### TC22 – Inner pages cannot be opened by URL without logging in

| Field | Details |
|-------|---------|
| Area | Security |
| Type | Negative |
| Priority | P2 |
| Automate? | Yes – one parameterized test over all inner pages |
| Status | Manual only (automation backlog) |

**Pre-conditions**

New browser session; never logged in

**Steps**

1. Open base URL + each path in the data column

**Test data**

- /cart.html
- /checkout-step-one.html
- /checkout-step-two.html
- /checkout-complete.html

**Expected result**

Each path redirects to the login page with the error "You can only access '<path>' when you are logged in."

---

### TC23 – Error messages are clear and can be dismissed

| Field | Details |
|-------|---------|
| Area | Login |
| Type | Usability |
| Priority | P3 |
| Automate? | No – clarity of wording needs human judgement |
| Status | Manual only |

**Pre-conditions**

On the login page

**Steps**

1. Click "Login" with both fields empty
2. Read the error and check the fields
3. Click the X on the error message

**Test data**

–

**Expected result**

The error states the problem in plain words; the fields are highlighted; clicking X closes the error and removes the highlight

---

### TC24 – Shop and check out on other browsers and screen sizes

| Field | Details |
|-------|---------|
| Area | Cross-browser |
| Type | Browser / screen size |
| Priority | P2 |
| Automate? | No – manual for now; later add as a cross-browser CI run |
| Status | Manual only |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"
2. Login with valid user credential

**Steps**

1. Run the login-to-Finish flow on Chrome, Firefox and Edge
2. Repeat at 375×667 (mobile) and 768×1024 (tablet)

**Test data**

Chrome, Firefox, Edge; 375×667, 768×1024

**Expected result**

Layout is readable with no overlapping elements or horizontal scrolling; menu, sort, cart and checkout buttons are usable at every size

---

### TC25 – Complete the purchase using only the keyboard

| Field | Details |
|-------|---------|
| Area | Accessibility |
| Type | Accessibility |
| Priority | P2 |
| Automate? | No – manual with an axe scan as support; the keyboard flow could be automated later |
| Status | Manual only |

**Pre-conditions**

On the login page

**Steps**

1. Using only Tab, Shift+Tab, Enter and Space, log in, sort, add an item and check out
2. Check that keyboard focus is always visible
3. Check images and fields with a screen reader or an axe scan

**Test data**

Keyboard only; axe DevTools

**Expected result**

Every action can be done by keyboard; focus is always visible; product images have meaningful alt text; every input has an accessible name

---

### TC26 – Reset App State clears the cart

| Field | Details |
|-------|---------|
| Area | Menu |
| Type | Positive |
| Priority | P3 |
| Automate? | Yes – cheap check of the cart reset |
| Status | Manual only (automation backlog) |

**Pre-conditions**

1. Launch "https://www.saucedemo.com/"
2. Login with valid user credential
3. Two items in cart

**Steps**

1. Open the menu and click "Reset App State"
2. Check the badge and the cart

**Test data**

–

**Expected result**

No badge is shown and the cart is empty

---
