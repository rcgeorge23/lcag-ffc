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

    static void verifyInitialPaymentFormQuestionsDisplayed(Browser browser) {
        browser.waitFor { browser.page.paymentDeclinedSection.displayed == false }
        browser.waitFor { browser.page.acceptTermsAndConditionsCheckbox.attr("disabled") == "true" }
        browser.waitFor { checkboxValue(browser.page.acceptTermsAndConditionsCheckbox) == true }
        browser.waitFor { browser.page.paymentFormSection.displayed == true }
        browser.waitFor { browser.page.existingLcagAccountYes.displayed == true }
        browser.waitFor { browser.page.existingLcagAccountNo.displayed == true }
        browser.waitFor { browser.page.existingLcagAccountAnonymous.displayed == true }
        browser.waitFor { browser.page.existingLcagAccountYes.value() == null }
        browser.waitFor { browser.page.existingLcagAccountNo.value() == null }
        browser.waitFor { browser.page.existingLcagAccountAnonymous.value() == null }
        browser.waitFor { browser.page.payNowButton.displayed == false }
    }

    static void verifyHappyInitialPaymentFormState(Browser browser) {
        browser.waitFor { browser.page.paymentDeclinedSection.displayed == false }
        browser.waitFor { browser.page.termsAndConditionsSection.displayed == true }
        browser.waitFor { browser.page.acceptTermsAndConditionsCheckbox.displayed == true }
        browser.waitFor { checkboxValue(browser.page.acceptTermsAndConditionsCheckbox) == false }
        browser.waitFor { browser.page.paymentFormSection.displayed == false }
        browser.waitFor { browser.page.payNowButton.displayed == false }
    }

    static void anonymousPaymentCreditCardFormDisplayed(Browser browser) {
        browser.waitFor { browser.page.donationInfoSection.displayed == true }
        browser.waitFor { browser.page.usernameInput.displayed == false }
        browser.waitFor { browser.page.contributionAgreementInfoSection.displayed == false }
        browser.waitFor { browser.page.contributionTypeDonation.displayed == true }
        browser.waitFor { browser.page.contributionTypeContributionAgreement.displayed == true }
        browser.waitFor { browser.page.contributionTypeDonation.value() == 'DONATION' }
        browser.waitFor { browser.page.contributionTypeContributionAgreement.value() == null }
        browser.waitFor { browser.page.contributionTypeDonation.attr("disabled") == "true" }
        browser.waitFor { browser.page.contributionTypeContributionAgreement.attr("disabled") == "true" }
        browser.waitFor { browser.page.payNowButton.displayed == true }
    }

    static void existingLcagUserAccountPaymentCreditCardFormDisplayed(Browser browser) {
        browser.waitFor { browser.page.donationInfoSection.displayed == false }
        browser.waitFor { browser.page.usernameInput.displayed == true }
        browser.waitFor { browser.page.contributionAgreementInfoSection.displayed == false }
        browser.waitFor { browser.page.contributionTypeDonation.displayed == true }
        browser.waitFor { browser.page.contributionTypeContributionAgreement.displayed == true }
        browser.waitFor { browser.page.contributionTypeDonation.value() == null }
        browser.waitFor { browser.page.contributionTypeContributionAgreement.value() == null }
        browser.waitFor { browser.page.contributionTypeDonation.attr("disabled") == "" }
        browser.waitFor { browser.page.contributionTypeContributionAgreement.attr("disabled") == "" }
        browser.waitFor { browser.page.payNowButton.displayed == true }
    }

    static void newLcagUserAccountPaymentCreditCardFormDisplayed(Browser browser) {
        browser.waitFor { browser.page.donationInfoSection.displayed == false }
        browser.waitFor { browser.page.usernameInput.displayed == false }
        browser.waitFor { browser.page.firstNameInput.displayed == true }
        browser.waitFor { browser.page.lastNameInput.displayed == true }
        browser.waitFor { browser.page.emailAddressInput.displayed == true }
        browser.waitFor { browser.page.newLcagJoinerInfoSection.displayed == true }
        browser.waitFor { browser.page.contributionAgreementInfoSection.displayed == false }
        browser.waitFor { browser.page.contributionTypeDonation.displayed == true }
        browser.waitFor { browser.page.contributionTypeContributionAgreement.displayed == true }
        browser.waitFor { browser.page.contributionTypeDonation.value() == null }
        browser.waitFor { browser.page.contributionTypeContributionAgreement.value() == null }
        browser.waitFor { browser.page.contributionTypeDonation.attr("disabled") == "" }
        browser.waitFor { browser.page.contributionTypeContributionAgreement.attr("disabled") == "" }
        browser.waitFor { browser.page.payNowButton.displayed == true }
    }
}
