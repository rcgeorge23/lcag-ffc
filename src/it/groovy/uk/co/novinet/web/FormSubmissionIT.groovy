package uk.co.novinet.web

import geb.spock.GebSpec

import static uk.co.novinet.e2e.TestUtils.*
import static uk.co.novinet.web.GebTestUtils.anonymousPaymentCreditCardFormDisplayed
import static uk.co.novinet.web.GebTestUtils.enterCardDetails
import static uk.co.novinet.web.GebTestUtils.existingLcagUserAccountPaymentCreditCardFormDisplayed
import static uk.co.novinet.web.GebTestUtils.verifyHappyInitialPaymentFormState
import static uk.co.novinet.web.GebTestUtils.verifyInitialPaymentFormQuestionsDisplayed

class FormSubmissionIT extends GebSpec {

    static final schemes = "schemes"
    public static final String AUTHORIZED_CARD = "4242424242424242"
    public static final String DECLINED_CARD = "4000000000000002"

    def setup() {
        setupDatabaseSchema()
        deleteAllMessages("user1@something.com")
        getEmails("user1@something.com", "Inbox").size() == 0
    }

    def "i can complete the payment flow as an anonymous donor"() {
        given:
            go "http://localhost:8484"

        when:
            waitFor { at LcagFfcFormPage }

        then:
            verifyHappyInitialPaymentFormState(browser)

        when: "i agree to the t&cs"
            acceptTermsAndConditionsCheckbox.click()

        then: "initial payment form questions appear and nothing is selected"
            verifyInitialPaymentFormQuestionsDisplayed(browser)

        when: "i click on the anonymous donation radio"
            existingLcagAccountAnonymous.click()

        then: "credit card form is displayed"
            anonymousPaymentCreditCardFormDisplayed(browser)

        when: "i enter valid values and click pay now"
            amountInput = "10.00"
            enterCardDetails(browser, AUTHORIZED_CARD, "0222", "111", "33333")
            payNowButton.click()

        then: "i land on the thank you page"
            waitFor { at ThankYouPage }
    }

    def "anonymous payment, card declined"() {
        given:
            go "http://localhost:8484"

        when:
            waitFor { at LcagFfcFormPage }

        then:
            verifyHappyInitialPaymentFormState(browser)

        when: "i agree to the t&cs"
            acceptTermsAndConditionsCheckbox.click()

        then: "initial payment form questions appear and nothing is selected"
            verifyInitialPaymentFormQuestionsDisplayed(browser)

        when: "i click on the anonymous donation radio"
            existingLcagAccountAnonymous.click()

        then: "credit card form is displayed"
            anonymousPaymentCreditCardFormDisplayed(browser)

        when: "i enter valid values and click pay now"
            amountInput = "10.00"
            enterCardDetails(browser, DECLINED_CARD, "0222", "111", "33333")
            payNowButton.click()

        then: "i remain on the same page with a payment declined banner"
            waitFor { at LcagFfcFormPage }
            waitFor { paymentDeclinedSection.displayed == true }
    }

    def "i can complete the payment flow for donation as an existing lcag member"() {
        given:
            go "http://localhost:8484"

        when:
            waitFor { at LcagFfcFormPage }

        then:
            verifyHappyInitialPaymentFormState(browser)

        when: "i agree to the t&cs"
            acceptTermsAndConditionsCheckbox.click()

        then: "initial payment form questions appear and nothing is selected"
            verifyInitialPaymentFormQuestionsDisplayed(browser)

        when: "i click on the anonymous donation radio"
            existingLcagAccountYes.click()

        then: "credit card form is displayed"
            existingLcagUserAccountPaymentCreditCardFormDisplayed(browser)

        when: "i enter valid lcag username value and payment details and click pay now"
            username = "testuser1"
            contributionTypeDonation.click()
            amountInput = "10.00"
            enterCardDetails(browser, AUTHORIZED_CARD, "0222", "111", "33333")
            payNowButton.click()

        then: "i land on the thank you page and i receive a confirmation email"
            waitFor { at ThankYouPage }
            waitFor { getEmails("user1@something.com", "Inbox").size() == 1 }
            waitFor { getEmails("user1@something.com", "Inbox").get(0).content.contains("Dear Test Name1, Thank you for your contribution of £10.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. Many thanks, LCAG FFC Team") }
    }

    def "payment declined for donation as an existing lcag member"() {
        given:
            go "http://localhost:8484"

        when:
            waitFor { at LcagFfcFormPage }

        then:
            verifyHappyInitialPaymentFormState(browser)

        when: "i agree to the t&cs"
            acceptTermsAndConditionsCheckbox.click()

        then: "initial payment form questions appear and nothing is selected"
            verifyInitialPaymentFormQuestionsDisplayed(browser)

        when: "i click on the anonymous donation radio"
            existingLcagAccountYes.click()

        then: "credit card form is displayed"
            existingLcagUserAccountPaymentCreditCardFormDisplayed(browser)

        when: "i enter valid lcag username value and payment details and click pay now"
            username = "testuser1"
            contributionTypeDonation.click()
            amountInput = "10.00"
            enterCardDetails(browser, DECLINED_CARD, "0222", "111", "33333")
            payNowButton.click()

        then: "i remain on the same page with a payment declined banner"
            waitFor { at LcagFfcFormPage }
            waitFor { paymentDeclinedSection.displayed == true }
            sleep(3000)
            waitFor { getEmails("user1@something.com", "Inbox").size() == 0 }
    }

//
//    def "claim participant form cannot be submitted when fields are blank"() {
//        given:
//            go "http://localhost:8484?token=claim_1"
//            waitFor { at LcagFfcFormPage }
//            firstNameInput.value("")
//            lastNameInput.value("")
//            emailAddressInput.value("")
//
//        when:
//            submitButton.click()
//
//        then:
//            waitFor { at LcagFfcFormPage }
//            waitFor { titleError.displayed }
//            assert titleError.displayed == true
//            assert firstNameError.displayed == true
//            assert lastNameError.displayed == true
//            assert emailAddressError.displayed == true
//            assert addressLine1Error.displayed == true
//            assert addressLine2Error.present == false
//            assert cityError.displayed == true
//            assert postcodeError.displayed == true
//            assert phoneNumberError.displayed == true
//            assert countryError.displayed == true
//            assert canShowWrittenEvidenceError.displayed == true
//            assert schemeDetailsError.displayed == true
//            assert schemeAdvisorDetailsError.displayed == true
//            assert additionalInformationError.present == false
//    }
//
//    def "claim participant form can be submitted when all mandatory fields are supplied"() {
//        given:
//            assert getClaimRows().size() == 0
//            go "http://localhost:8484?token=claim_1"
//            waitFor { at LcagFfcFormPage }
//            titleInput.value("title")
//            firstNameInput.value("firstName")
//            lastNameInput.value("lastName")
//            emailAddressInput.value("email@address.com")
//            addressLine1Input.value("addressLine1")
//            addressLine2Input.value("addressLine2")
//            cityInput.value("city")
//            postcodeInput.value("postcode")
//            countryInput.value("country")
//            phoneNumberInput.value("phoneNumber")
//            canShowWrittenEvidenceYes.click()
//            schemeDetailsInput.value("schemeDetails")
//            schemeAdvisorDetailsInput.value("schemeAdvisorDetails")
//            additionalInformationInput.value("additionalInformation")
//
//        when:
//            submitButton.click()
//
//        then:
//            waitFor { at ThankYouPage }
//            assert getClaimRows().get(0).title == "title"
//            assert getClaimRows().get(0).firstName == "firstName"
//            assert getClaimRows().get(0).lastName == "lastName"
//            assert getClaimRows().get(0).emailAddress == "email@address.com"
//            assert getClaimRows().get(0).addressLine1 == "addressLine1"
//            assert getClaimRows().get(0).addressLine2 == "addressLine2"
//            assert getClaimRows().get(0).city == "city"
//            assert getClaimRows().get(0).postcode == "postcode"
//            assert getClaimRows().get(0).country == "country"
//            assert getClaimRows().get(0).phoneNumber == "phoneNumber"
//            assert getClaimRows().get(0).canShowWrittenEvidence == "yes"
//            assert getClaimRows().get(0).schemeDetails == "schemeDetails"
//            assert getClaimRows().get(0).schemeAdvisorDetails == "schemeAdvisorDetails"
//            assert getClaimRows().get(0).additionalInformation == "additionalInformation"
//            assert getUserRows().get(0).hasCompletedClaimParticipantForm == true
//            assert getUserRows().get(0).hasBeenSentClaimConfirmationEmail == true
//    }
//
//    def "landing page is thank you page when already submitted"() {
//        given:
//            assert getClaimRows().size() == 0
//            runSqlUpdate("update `i7b0_users` set " +
//                    "has_completed_claim_participant_form = '" + 1 + "' " +
//                    "where uid = 1"
//            )
//
//        when:
//            go "http://localhost:8484?token=claim_1"
//            waitFor { at ThankYouPage }
//
//        then:
//            at ThankYouPage
//    }
//
//    def "landing page is claim participant form page when has submitted membership form"() {
//        given:
//            assert getClaimRows().size() == 0
//            runSqlUpdate("update `i7b0_users` set " +
//                    "has_completed_membership_form = '" + 1 + "' " +
//                    "where uid = 1"
//            )
//
//        when:
//            go "http://localhost:8484?token=claim_1"
//            waitFor { at LcagFfcFormPage }
//
//        then:
//            at LcagFfcFormPage
//    }

}
