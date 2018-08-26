package uk.co.novinet.web

import geb.Browser

class GebTestUtils {
    static boolean switchToGuestVerificationTabIfNecessaryAndAssertGridHasNRows(Browser browser, int expectedNumberOfRows) {
        if (expectedNumberOfRows == 0) {
            assert browser.guestsAwaitingVerificationTab.text() == "Guests Awaiting Verification"
        } else {
            assert browser.guestsAwaitingVerificationTab.text() == "Guests Awaiting Verification *"
        }

        if (!browser.verificationGrid.displayed) {
            browser.guestsAwaitingVerificationTab.click()
        }

        browser.waitFor { browser.verificationGridRows.size() == expectedNumberOfRows + 1 }

        return true
    }

    static boolean switchToMemberTabIfNecessaryAndAssertGridHasNRows(Browser browser, int expectedNumberOfRows) {
        if (!browser.memberGrid.displayed) {
            browser.waitFor { browser.memberTab.click() }
        }

        browser.waitFor { browser.memberGridRows.size() == expectedNumberOfRows + 1 }

        return true
    }

    static boolean switchToPaymentsTabIfNecessaryAndAssertGridHasNRows(Browser browser, int expectedNumberOfRows) {
        if (!browser.paymentsTab.displayed) {
            browser.waitFor { browser.paymentsTab.click() }
        }

        browser.waitFor { browser.paymentsGridRows.size() == expectedNumberOfRows + 1 }

        return true
    }

    static void enterCardDetails(Browser browser, String creditCardNumber, String expiryMMYY, String cv2, String postalCode) {
        browser.withFrame(browser.$("iframe")[0]) {
            browser.page.creditCardInput = creditCardNumber
            browser.page.expiryDateInput = expiryMMYY
            browser.page.c2vInput = cv2
            browser.page.postalCodeInput = postalCode
        }
    }

    static boolean checkboxValue(Object checkboxElement) {
        return checkboxElement.value() == "on"
    }
}
