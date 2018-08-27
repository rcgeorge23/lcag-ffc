package uk.co.novinet.web

import geb.spock.GebSpec
import uk.co.novinet.e2e.TestUtils
import uk.co.novinet.service.MemberService
import uk.co.novinet.service.PasswordDetails

import static uk.co.novinet.e2e.TestUtils.*
import static uk.co.novinet.web.GebTestUtils.anonymousPaymentCreditCardFormDisplayed
import static uk.co.novinet.web.GebTestUtils.enterCardDetails
import static uk.co.novinet.web.GebTestUtils.existingLcagUserAccountPaymentCreditCardFormDisplayed
import static uk.co.novinet.web.GebTestUtils.newLcagUserAccountPaymentCreditCardFormDisplayed
import static uk.co.novinet.web.GebTestUtils.verifyHappyInitialPaymentFormState
import static uk.co.novinet.web.GebTestUtils.verifyInitialPaymentFormQuestionsDisplayed

class FormSubmissionIT extends GebSpec {

    public static final String AUTHORIZED_CARD = "4242424242424242"
    public static final String DECLINED_CARD = "4000000000000002"

    //TESTS TO ADD:
    //Check that lcag ffc contributor group is added to profile of existing member
    //Check that lcag ffc contributor group is added to profile of newly created member
    //Server side validation
    //Client side validation combinations
    //Re-populating form after submission when error occurs

    def setup() {
        TestUtils.setupDatabaseSchema()

        deleteAllMessages("user1@something.com")
        deleteAllMessages("harry@generous.com")
        getEmails("user1@something.com", "Inbox").size() == 0
        getEmails("harry@generous.com", "Inbox").size() == 0
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
            waitFor { paymentReference.text() == "LCAGFFC90001" }
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
            insertUser(1, "testuser1", "user1@something.com", "Test Name1", 8, "1234_1", "claim_1")
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
            waitFor { paymentReference.text() == "LCAGFFC90001" }
            waitFor { getEmails("user1@something.com", "Inbox").size() == 1 }
            waitFor { getEmails("user1@something.com", "Inbox").get(0).content.contains("Dear Test Name1, Thank you for your contribution of £10.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. Many thanks, LCAG FFC Team") }
    }

    def "payment declined for donation as an existing lcag member"() {
        given:
            insertUser(1, "testuser1", "user1@something.com", "Test Name1", 8, "1234_1", "claim_1")
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

    def "i can complete the payment flow for donation for a new lcag applicant"() {
        given:
            sleep(3000) //wait for member cache to be refreshed
            getUserRows().size() == 0
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
            existingLcagAccountNo.click()

        then: "credit card form is displayed"
            GebTestUtils.newLcagUserAccountPaymentCreditCardFormDisplayed(browser)

        when: "i enter valid lcag username value and payment details and click pay now"
            contributionTypeDonation.click()
            firstNameInput = "Harry"
            lastNameInput = "Generous"
            emailAddressInput = "harry@generous.com"
            amountInput = "200.00"
            enterCardDetails(browser, AUTHORIZED_CARD, "0222", "111", "33333")
            payNowButton.click()
            waitFor { at ThankYouPage }
            waitFor { getEmails("harry@generous.com", "Inbox").size() == 1 }
            String emailContent = getEmails("harry@generous.com", "Inbox").get(0).content

        then: "i land on the thank you page and i receive a confirmation email"
            waitFor { at ThankYouPage }
            waitFor { paymentReference.text() == "LCAGFFC90001" }
            waitFor { getUserRows().size() == 1 }
            waitFor { getUserRows().get(0).emailAddress == "harry@generous.com" }
            waitFor { getUserRows().get(0).name == "Harry Generous" }
            waitFor { getEmails("harry@generous.com", "Inbox").size() == 1 }
            waitFor { emailContent.contains("Dear Harry Generous, Thank you for your contribution of £200.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. You have indicated that you would like to join LCAG and be kept up to date with the latest developments regarding the 2019 Loan Charge legal challenge. We have already set up a forum user account for you: Username: harry Temporary password:") }
            waitFor { emailContent.contains("Temporary password: 2019l0anCharg3") || emailContent.contains("Temporary password: lc4g2019Ch4rg3") || emailContent.contains("Temporary password: hm7cL04nch4rGe") || emailContent.contains("Temporary password: ch4l1Eng3Hm7C") }
            waitFor { emailContent.contains("You can access the forum from here: https://forum.hmrcloancharge.info/ Initially your ability to interact on the forum will be limited to the ‘Guest’ and ‘Welcome’ areas. This restriction will be lifted once we have verified your identity. In order to verify your identity we need to collect some additional information. If you are happy to proceed, please complete the LCAG membership form and we will get back to you as soon as we can: https://membership.hmrcloancharge.info?token=${getUserRows().get(0).membershipToken} Many thanks, LCAG FFC Team") }
    }

}
