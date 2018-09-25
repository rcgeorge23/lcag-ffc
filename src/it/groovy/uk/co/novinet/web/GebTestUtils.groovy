package uk.co.novinet.web

import geb.Browser
import org.openqa.selenium.Keys
import uk.co.novinet.rest.PaymentType

import java.text.SimpleDateFormat

import static uk.co.novinet.e2e.TestUtils.getEmails

class GebTestUtils {

    static void enterCardDetails(Browser browser, String creditCardNumber, String expiryMMYY, String cv2, String postalCode) {
        browser.withFrame(browser.$("iframe")[0]) {
            browser.page.creditCardInput = creditCardNumber
            browser.page.expiryDateInput = expiryMMYY
            browser.page.c2vInput = cv2
            browser.page.paymentFormPostalCodeInput = postalCode
        }
    }

    static void verifyInitialPaymentFormQuestionsDisplayed(Browser browser) {
        browser.waitFor { browser.page.paymentDeclinedSection.displayed == false }
        browser.waitFor { browser.page.acceptTermsAndConditionsButton.attr("disabled") == "true" }
        browser.waitFor { browser.page.paymentFormSection.displayed == true }
        browser.waitFor { browser.page.existingLcagAccountYes.displayed == false }
        browser.waitFor { browser.page.existingLcagAccountNo.displayed == false }
        browser.waitFor { browser.page.existingLcagAccountYes.value() == null }
        browser.waitFor { browser.page.existingLcagAccountNo.value() == null }
        browser.waitFor { browser.page.payNowButton.displayed == false }
        browser.waitFor { browser.page.amountInput.displayed == true }
        browser.waitFor { browser.page.donationInfoSection.displayed == false }
        browser.waitFor { browser.page.usernameInput.displayed == false }
        browser.waitFor { browser.page.firstNameInput.displayed == false }
        browser.waitFor { browser.page.lastNameInput.displayed == false }
        browser.waitFor { browser.page.emailAddressInput.displayed == false }
        browser.waitFor { browser.page.contributionAgreementInfoSection.displayed == false }
    }

    static void verifyHappyInitialPaymentFormState(Browser browser) {
        browser.waitFor { browser.page.paymentDeclinedSection.present == false }
        browser.waitFor { browser.page.termsAndConditionsSection.displayed == true }
        browser.waitFor { browser.page.acceptTermsAndConditionsButton.displayed == true }
        browser.waitFor { browser.page.paymentFormSection.displayed == false }
        browser.waitFor { browser.page.payNowButton.displayed == false }
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

    static void contributionAgreementAddressFieldsAreDisplayed(Browser browser, boolean displayed, int contributionAmount) {
        browser.waitFor { browser.page.addressLine1Input.displayed == displayed }
        browser.waitFor { browser.page.addressLine2Input.displayed == displayed }
        browser.waitFor { browser.page.cityInput.displayed == displayed }
        browser.waitFor { browser.page.postalCodeInput.displayed == displayed }
        browser.waitFor { browser.page.countryInput.displayed == displayed }
        browser.waitFor { browser.page.contributionAgreementInfoSection.displayed == (contributionAmount >= 600) }
        browser.waitFor { browser.page.donationInfoSection.displayed == (contributionAmount < 600) }
    }

    static boolean verifyNoVatNumberInvoice(
            Browser browser,
            String reference,
            Date date,
            String paymentMethod,
            String recipientName,
            String recipientEmail,
            String grossAmount) {
        assert browser.page.reference.text() == reference
        assert browser.page.invoiceCreatedDate.text() == new SimpleDateFormat("dd MMM yyyy").format(date)
        assert browser.page.paymentReceivedDate.text() == new SimpleDateFormat("dd MMM yyyy").format(date)
        assert browser.page.paymentMethod.text() == paymentMethod
        assert browser.page.invoiceRecipientName.text() == recipientName
        assert browser.page.invoiceRecipientEmailAddress.text() == recipientEmail
        assert browser.page.contributionType.text() == "Contribution Agreement"
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

    static boolean verifyAttachment(String emailAddress, int attachmentIndex, int expectedNumberOfAttachments, String expectedFileName, int emailIndex = 0) {
        assert getEmails(emailAddress, "Inbox").get(emailIndex).getAttachments().size() == expectedNumberOfAttachments
        assert getEmails(emailAddress, "Inbox").get(emailIndex).getAttachments().get(attachmentIndex).getFilename().equals(expectedFileName)
        assert getEmails(emailAddress, "Inbox").get(emailIndex).getAttachments().get(attachmentIndex).getBytes().length > 0
        return true
    }

    static boolean verifyContributionAgreement(
            Browser browser,
            Date date,
            String name,
            String addressLine1,
            String addressLine2,
            String city,
            String postalCode,
            String country,
            String contributionAmount) {
        assert browser.page.contributionAgreementDate == new SimpleDateFormat("dd MMM yyyy").format(date)

        assert browser.page.addressLine1 == addressLine1
        assert browser.page.addressLine2 == addressLine2
        assert browser.page.city == city
        assert browser.page.postalCode == postalCode
        assert browser.page.country == country
        assert browser.page.grossAmount == contributionAmount
        assert browser.page.contributorName == name

        return true
    }

    static void driveToPaymentType(Browser browser, String paymentAmount, PaymentType paymentType) {
        browser.go("http://localhost:8484")
        browser.waitFor { browser.at LcagFfcFormPage }
        verifyHappyInitialPaymentFormState(browser)
        browser.page.acceptTermsAndConditionsButton.click()
        verifyInitialPaymentFormQuestionsDisplayed(browser)
        browser.page.amountInput = paymentAmount

        switch (paymentType) {
            case (PaymentType.NEW_LCAG_MEMBER):
                browser.page.existingLcagAccountNo.click()
                break
            case (PaymentType.EXISTING_LCAG_MEMBER):
                browser.page.existingLcagAccountYes.click()
                break
        }

//        newLcagUserAccountPaymentCreditCardFormDisplayed(browser)
//        existingLcagUserAccountPaymentCreditCardFormDisplayed(browser)
//        anonymousPaymentCreditCardFormDisplayed(browser)
    }
}
