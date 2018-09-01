package uk.co.novinet.web

import geb.Browser
import org.springframework.format.number.CurrencyStyleFormatter

import java.text.SimpleDateFormat

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
        browser.waitFor { browser.page.acceptTermsAndConditionsButton.attr("disabled") == "true" }
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
        browser.waitFor { browser.page.acceptTermsAndConditionsButton.displayed == true }
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

    static boolean verifyInvoice(
            Browser browser,
            String reference,
            Date date,
            String paymentMethod,
            String recipientName,
            String recipientEmail,
            String contributionType,
            String netAmount,
            String vatPercentage,
            String vatAmount,
            String grossAmount) {
        assert browser.page.reference.text() == reference
        assert browser.page.invoiceCreatedDate.text() == new SimpleDateFormat("dd MMM yyyy").format(date)
        assert browser.page.paymentReceivedDate.text() == new SimpleDateFormat("dd MMM yyyy").format(date)
        assert browser.page.paymentMethod.text() == paymentMethod
        assert browser.page.invoiceRecipientName.text() == recipientName
        assert browser.page.invoiceRecipientEmailAddress.text() == recipientEmail
        assert browser.page.contributionType.text() == contributionType
        assert browser.page.netAmount.text() == netAmount
        assert browser.page.vatPercentage.text() == vatPercentage
        assert browser.page.vatAmount.text() == vatAmount
        assert browser.page.grossAmount.text() == grossAmount
        return true
    }
}
