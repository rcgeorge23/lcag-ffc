package uk.co.novinet.web

import geb.Browser
import org.springframework.format.number.CurrencyStyleFormatter

import java.text.SimpleDateFormat

import static uk.co.novinet.e2e.TestUtils.getEmails
import static uk.co.novinet.e2e.TestUtils.getEmails
import static uk.co.novinet.e2e.TestUtils.getEmails

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
            browser.page.paymentFormPostalCodeInput = postalCode
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

    static void contributionAgreementAddressFieldsAreDisplayed(Browser browser, boolean displayed) {
        browser.waitFor { browser.page.addressLine1Input.displayed == displayed }
        browser.waitFor { browser.page.addressLine2Input.displayed == displayed }
        browser.waitFor { browser.page.cityInput.displayed == displayed }
        browser.waitFor { browser.page.postalCodeInput.displayed == displayed }
        browser.waitFor { browser.page.countryInput.displayed == displayed }
        browser.waitFor { browser.page.contributionAgreementInfoSection.displayed == displayed }
        browser.waitFor { browser.page.donationInfoSection.displayed == !displayed }
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

    static void enterContributionAgreementAddressDetails(Browser browser, String firstName, String lastName, String emailAddress) {
        browser.page.firstNameInput = firstName
        browser.page.lastNameInput = lastName
        browser.page.emailAddressInput = emailAddress
        browser.page.addressLine1Input = "10 Some Street"
        browser.page.addressLine2Input = "Some Village"
        browser.page.cityInput = "Some City"
        browser.page.postalCodeInput = "Some Postcode"
        browser.page.countryInput = "Some Country"
    }

    static boolean verifyAttachment(String emailAddress, int index, int expectedNumberOfAttachments, String expectedFileName) {
        assert getEmails(emailAddress, "Inbox").get(0).getAttachments().size() == expectedNumberOfAttachments
        assert getEmails(emailAddress, "Inbox").get(0).getAttachments().get(index).getFilename().equals(expectedFileName)
        assert getEmails(emailAddress, "Inbox").get(0).getAttachments().get(index).getBytes().length > 0
        return true
    }

    static boolean verifyNoAttachments(String emailAddress) {
        return getEmails(emailAddress, "Inbox").get(0).getAttachments().size() == 0
    }

    static boolean verifyContributionAgreement(Browser browser, Date date, String name, String addressLine1, String addressLine2, String city, String postalCode, String country, String contributionAmount) {
        assert browser.page.contributionAgreementDate == new SimpleDateFormat("dd MMM yyyy").format(date)
        assert browser.page.contributorName == name
        assert browser.page.addressLine1 == addressLine1
        assert browser.page.addressLine2 == addressLine2
        assert browser.page.city == city
        assert browser.page.postalCode == postalCode
        assert browser.page.country == country
        assert browser.page.grossAmount == contributionAmount
        return true
    }
}
