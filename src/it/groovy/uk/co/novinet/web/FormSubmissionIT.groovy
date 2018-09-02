package uk.co.novinet.web

import geb.spock.GebSpec
import uk.co.novinet.e2e.TestUtils
import uk.co.novinet.service.ContributionType
import static uk.co.novinet.e2e.TestUtils.*
import static uk.co.novinet.web.GebTestUtils.*

import java.text.SimpleDateFormat

import static org.apache.commons.lang3.StringUtils.isBlank
class FormSubmissionIT extends GebSpec {

    public static final String AUTHORIZED_CARD = "4242424242424242"
    public static final String DECLINED_CARD = "4000000000000002"

    //TESTS TO ADD:
    //Server side validation
    //Client side validation combinations
    //Re-populating form after submission when error occurs
    //Add a message to the front end form saying that amount is inclusive of VAT for contribution agreements
    //Add the contribution agreement attachment (with RH's signature) - DONE
    //Check that lcag ffc contributor group is added to profile of existing member - DONE
    //Check that lcag ffc contributor group is added to profile of newly created member - DONE
    //Donation confirmation email DOES NOT have an invoice attachment - DONE
    //Contribution agreement email DOES have an invoice attachment - DONE

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
            acceptTermsAndConditionsButton.click()

        then: "initial payment form questions appear and nothing is selected"
            verifyInitialPaymentFormQuestionsDisplayed(browser)

        when: "i click on the anonymous donation radio"
            existingLcagAccountAnonymous.click()

        then: "credit card form is displayed"
            anonymousPaymentCreditCardFormDisplayed(browser)

        and: "contribution agreement fields are not displayed"
            contributionAgreementAddressFieldsAreDisplayed(browser, false)

        when: "i enter valid values and click pay now"
            amountInput = "10.00"
            enterCardDetails(browser, AUTHORIZED_CARD, "0222", "111", "33333")
            payNowButton.click()

        then: "i land on the thank you page"
            waitFor { at ThankYouPage }
            waitFor { paymentReference.text() == "LCAGFFC90001" }

        when: "i navigate to the invoice page"
            go driver.currentUrl.replace("thankYou", "invoice")
            waitFor { at InvoicePage }

        then: "i navigate to the invoice page"
            verifyNoVatNumberInvoice(browser, "LCAGFFC90001", new Date(), "Card", "", "", "Donation", "£10.00")

        when: "i set the vat number and refresh the page"
            runSqlUpdate("update i7b0_ffc_contributions set vat_number = '1234567890' where `reference` = 'LCAGFFC90001'")
            driver.navigate().refresh()
            waitFor { at InvoicePage }

        then:
            verifyInvoice(browser, "LCAGFFC90001", new Date(), "Card", "", "", "Donation", "£10.00", "0%", "£0.00", "£10.00", "1234567890")
    }

    def "anonymous payment, card declined"() {
        given:
            go "http://localhost:8484"

        when:
            waitFor { at LcagFfcFormPage }

        then:
            verifyHappyInitialPaymentFormState(browser)

        when: "i agree to the t&cs"
            acceptTermsAndConditionsButton.click()

        then: "initial payment form questions appear and nothing is selected"
            verifyInitialPaymentFormQuestionsDisplayed(browser)

        when: "i click on the anonymous donation radio"
            existingLcagAccountAnonymous.click()

        then: "credit card form is displayed"
            anonymousPaymentCreditCardFormDisplayed(browser)

        and: "contribution agreement fields are not displayed"
            contributionAgreementAddressFieldsAreDisplayed(browser, false)

        when: "i enter valid values and click pay now"
            amountInput = "10.00"
            enterCardDetails(browser, DECLINED_CARD, "0222", "111", "33333")
            payNowButton.click()

        then: "i remain on the same page with a payment declined banner"
            waitFor { at LcagFfcFormPage }
            waitFor { paymentDeclinedSection.displayed == true }
    }

    def "i can complete the payment flow for donation of less than £250 as an existing lcag member"() {
        given:
            insertUser(1, "testuser1", "user1@something.com", "Test Name1", 8, "1234_1", "claim_1")
            isBlank(getUserRows().get(0).getAdditionalGroups())
            go "http://localhost:8484"

        when:
            waitFor { at LcagFfcFormPage }

        then:
            verifyHappyInitialPaymentFormState(browser)

        when: "i agree to the t&cs"
            acceptTermsAndConditionsButton.click()

        then: "initial payment form questions appear and nothing is selected"
            verifyInitialPaymentFormQuestionsDisplayed(browser)

        when: "i click on the anonymous donation radio"
            existingLcagAccountYes.click()

        then: "credit card form is displayed"
            existingLcagUserAccountPaymentCreditCardFormDisplayed(browser)

        when: "i enter valid lcag username value and payment details and click pay now"
            username = "testuser1"
            contributionTypeDonation.click()
            contributionAgreementAddressFieldsAreDisplayed(browser, false)
            donationInfoSection.displayed == true
            contributionAgreementInfoSection.displayed == false
            amountInput = "10.00"
            enterCardDetails(browser, AUTHORIZED_CARD, "0222", "111", "33333")
            payNowButton.click()

        then: "i land on the thank you page and i receive a confirmation email"
            waitFor { at ThankYouPage }
            waitFor { paymentReference.text() == "LCAGFFC90001" }
            waitFor { getEmails("user1@something.com", "Inbox").size() == 1 }
            waitFor { getEmails("user1@something.com", "Inbox").get(0).content.contains("Dear Test Name1, Thank you for your contribution of £10.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. Many thanks, LCAG FFC Team") }
            waitFor { getUserRows().get(0).getGroup() == "8" }
            waitFor { getUserRows().get(0).getAdditionalGroups() == "9" }
            GebTestUtils.verifyNoAttachments("user1@something.com")

        when: "i navigate to the invoice page"
            go driver.currentUrl.replace("thankYou", "invoice")
            waitFor { at InvoicePage }

        then:
            verifyNoVatNumberInvoice(browser, "LCAGFFC90001", new Date(), "Card", "Test Name1", "user1@something.com", "Donation", "£10.00")

        when: "i set the vat number and refresh the page"
            runSqlUpdate("update i7b0_ffc_contributions set vat_number = '1234567890' where `reference` = 'LCAGFFC90001'")
            driver.navigate().refresh()
            waitFor { at InvoicePage }

        then:
            verifyInvoice(browser, "LCAGFFC90001", new Date(), "Card", "Test Name1", "user1@something.com", "Donation", "£10.00", "0%", "£0.00", "£10.00", "1234567890")
    }

    def "i can complete the payment flow for donation of £250 as an existing lcag member"() {
        given:
            insertUser(1, "testuser1", "user1@something.com", "Test Name1", 8, "1234_1", "claim_1")
            isBlank(getUserRows().get(0).getAdditionalGroups())
            go "http://localhost:8484"

        when:
            waitFor { at LcagFfcFormPage }

        then:
            verifyHappyInitialPaymentFormState(browser)

        when: "i agree to the t&cs"
            acceptTermsAndConditionsButton.click()

        then: "initial payment form questions appear and nothing is selected"
            verifyInitialPaymentFormQuestionsDisplayed(browser)

        when: "i click on the anonymous donation radio"
            existingLcagAccountYes.click()

        then: "credit card form is displayed"
            existingLcagUserAccountPaymentCreditCardFormDisplayed(browser)

        when: "i enter valid lcag username value and payment details and click pay now"
            username = "testuser1"
            contributionTypeDonation.click()
            contributionAgreementAddressFieldsAreDisplayed(browser, false)
            donationInfoSection.displayed == true
            contributionAgreementInfoSection.displayed == false
            amountInput = "250.00"
            enterCardDetails(browser, AUTHORIZED_CARD, "0222", "111", "33333")
            payNowButton.click()

        then: "i land on the thank you page and i receive a confirmation email"
            waitFor { at ThankYouPage }
            waitFor { paymentReference.text() == "LCAGFFC90001" }
            waitFor { getEmails("user1@something.com", "Inbox").size() == 1 }
            waitFor { getEmails("user1@something.com", "Inbox").get(0).content.contains("Dear Test Name1, Thank you for your contribution of £250.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. Many thanks, LCAG FFC Team") }
            waitFor { getUserRows().get(0).getGroup() == "8" }
            waitFor { getUserRows().get(0).getAdditionalGroups() == "9,10" }
            GebTestUtils.verifyNoAttachments("user1@something.com")

        when: "i navigate to the invoice page"
            go driver.currentUrl.replace("thankYou", "invoice")
            waitFor { at InvoicePage }

        then:
            verifyNoVatNumberInvoice(browser, "LCAGFFC90001", new Date(), "Card", "Test Name1", "user1@something.com", "Donation", "£250.00")

        when: "i set the vat number and refresh the page"
            runSqlUpdate("update i7b0_ffc_contributions set vat_number = '1234567890' where `reference` = 'LCAGFFC90001'")
            driver.navigate().refresh()
            waitFor { at InvoicePage }

        then:
            verifyInvoice(browser, "LCAGFFC90001", new Date(), "Card", "Test Name1", "user1@something.com", "Donation", "£250.00", "0%", "£0.00", "£250.00", "1234567890")
    }

    def "i can complete the payment flow for contribution agreement as an existing lcag member"() {
        given:
            insertUser(1, "testuser1", "user1@something.com", "Test Name1", 8, "1234_1", "claim_1")
            isBlank(getUserRows().get(0).getAdditionalGroups())
            go "http://localhost:8484"

        when:
            waitFor { at LcagFfcFormPage }

        then:
            verifyHappyInitialPaymentFormState(browser)

        when: "i agree to the t&cs"
            acceptTermsAndConditionsButton.click()

        then: "initial payment form questions appear and nothing is selected"
            verifyInitialPaymentFormQuestionsDisplayed(browser)

        when: "i click on the anonymous donation radio"
            existingLcagAccountYes.click()

        then: "credit card form is displayed"
            existingLcagUserAccountPaymentCreditCardFormDisplayed(browser)

        when: "i enter valid lcag username value and payment details and click pay now"
            username = "testuser1"
            contributionTypeContributionAgreement.click()
            contributionAgreementAddressFieldsAreDisplayed(browser, true)
            enterContributionAgreementAddressDetails(browser, "John", "Smith", "user1@something.com")
            amountInput = "1000.00"
            enterCardDetails(browser, AUTHORIZED_CARD, "0222", "111", "33333")
            payNowButton.click()

        then: "i land on the thank you page and i receive a confirmation email"
            waitFor { at ThankYouPage }
            waitFor { paymentReference.text() == "LCAGFFC90001" }
            waitFor { getEmails("user1@something.com", "Inbox").size() == 1 }
            waitFor { getEmails("user1@something.com", "Inbox").get(0).content.contains("Dear John Smith, Thank you for your contribution of £1,000.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. Many thanks, LCAG FFC Team") }
            waitFor { getUserRows().get(0).getGroup() == "8" }
            waitFor { getUserRows().get(0).getAdditionalGroups() == "9,10" }
            verifyAttachment("user1@something.com", 0, 2, "lcag-ffc-payment-invoice-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf")
            verifyAttachment("user1@something.com", 1, 2, "lcag-ffc-contribution-agreement-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf")

        when: "i navigate to the invoice page"
            go driver.currentUrl.replace("thankYou", "invoice")
            waitFor { at InvoicePage }

        then:
            verifyNoVatNumberInvoice(browser, "LCAGFFC90001", new Date(), "Card", "John Smith", "user1@something.com", "Contribution Agreement", "£1,000.00")

        when: "i set the vat number and refresh the page"
            runSqlUpdate("update i7b0_ffc_contributions set vat_number = '1234567890' where `reference` = 'LCAGFFC90001'")
            driver.navigate().refresh()
            waitFor { at InvoicePage }

        then:
            verifyInvoice(browser, "LCAGFFC90001", new Date(), "Card", "John Smith", "user1@something.com", "Contribution Agreement", "£833.33", "20%", "£166.67", "£1,000.00", "1234567890")

        when: "i navigate to the invoice page"
            go driver.currentUrl.replace("invoice", "contributionAgreement")
            waitFor { at ContributionAgreementPage }

        then:
            GebTestUtils.verifyContributionAgreement(browser, new Date(), "John Smith", "10 Some Street", "Some Village", "Some City", "Some Postcode", "Some Country", "£1,000.00")
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
            acceptTermsAndConditionsButton.click()

        then: "initial payment form questions appear and nothing is selected"
            verifyInitialPaymentFormQuestionsDisplayed(browser)

        when: "i click on the anonymous donation radio"
            existingLcagAccountYes.click()

        then: "credit card form is displayed"
            existingLcagUserAccountPaymentCreditCardFormDisplayed(browser)

        when: "i enter valid lcag username value and payment details and click pay now"
            username = "testuser1"
            contributionTypeDonation.click()
            contributionAgreementAddressFieldsAreDisplayed(browser, false)
            amountInput = "10.00"
            enterCardDetails(browser, DECLINED_CARD, "0222", "111", "33333")
            payNowButton.click()

        then: "i remain on the same page with a payment declined banner"
            waitFor { at LcagFfcFormPage }
            waitFor { paymentDeclinedSection.displayed == true }
            sleep(3000)
            waitFor { getEmails("user1@something.com", "Inbox").size() == 0 }
            waitFor { getUserRows().get(0).getGroup() == "8" }
            waitFor { isBlank(getUserRows().get(0).getAdditionalGroups()) }
    }

    def "i can complete the payment flow for donation for a new lcag applicant for less than £250"() {
        given:
            sleep(3000) //wait for member cache to be refreshed
            getUserRows().size() == 0
            go "http://localhost:8484"

        when:
            waitFor { at LcagFfcFormPage }

        then:
            verifyHappyInitialPaymentFormState(browser)

        when: "i agree to the t&cs"
            acceptTermsAndConditionsButton.click()

        then: "initial payment form questions appear and nothing is selected"
            verifyInitialPaymentFormQuestionsDisplayed(browser)

        when: "i click on the anonymous donation radio"
            existingLcagAccountNo.click()

        then: "credit card form is displayed"
            newLcagUserAccountPaymentCreditCardFormDisplayed(browser)

        when: "i enter valid lcag username value and payment details and click pay now"
            contributionTypeDonation.click()
            contributionAgreementAddressFieldsAreDisplayed(browser, false)
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
            waitFor { getUserRows().get(0).getGroup() == "8" }
            waitFor { getUserRows().get(0).getAdditionalGroups() == "9" }
            GebTestUtils.verifyNoAttachments("harry@generous.com")

        when: "i navigate to the invoice page"
            go driver.currentUrl.replace("thankYou", "invoice")
            waitFor { at InvoicePage }

        then:
            verifyNoVatNumberInvoice(browser, "LCAGFFC90001", new Date(), "Card", "Harry Generous", "harry@generous.com", "Donation", "£200.00")

        when: "i set the vat number and refresh the page"
            runSqlUpdate("update i7b0_ffc_contributions set vat_number = '1234567890' where `reference` = 'LCAGFFC90001'")
            driver.navigate().refresh()
            waitFor { at InvoicePage }

        then:
            verifyInvoice(browser, "LCAGFFC90001", new Date(), "Card", "Harry Generous", "harry@generous.com", "Donation", "£200.00", "0%", "£0.00", "£200.00", "1234567890")
    }

    def "i can complete the payment flow for donation for a new lcag applicant for £250"() {
        given:
            sleep(3000) //wait for member cache to be refreshed
            getUserRows().size() == 0
            go "http://localhost:8484"

        when:
            waitFor { at LcagFfcFormPage }

        then:
            verifyHappyInitialPaymentFormState(browser)

        when: "i agree to the t&cs"
            acceptTermsAndConditionsButton.click()

        then: "initial payment form questions appear and nothing is selected"
            verifyInitialPaymentFormQuestionsDisplayed(browser)

        when: "i click on the anonymous donation radio"
            existingLcagAccountNo.click()

        then: "credit card form is displayed"
            newLcagUserAccountPaymentCreditCardFormDisplayed(browser)

        when: "i enter valid lcag username value and payment details and click pay now"
            contributionTypeDonation.click()
            contributionAgreementAddressFieldsAreDisplayed(browser, false)
            firstNameInput = "Harry"
            lastNameInput = "Generous"
            emailAddressInput = "harry@generous.com"
            amountInput = "250.00"
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
            waitFor { emailContent.contains("Dear Harry Generous, Thank you for your contribution of £250.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. You have indicated that you would like to join LCAG and be kept up to date with the latest developments regarding the 2019 Loan Charge legal challenge. We have already set up a forum user account for you: Username: harry Temporary password:") }
            waitFor { emailContent.contains("Temporary password: 2019l0anCharg3") || emailContent.contains("Temporary password: lc4g2019Ch4rg3") || emailContent.contains("Temporary password: hm7cL04nch4rGe") || emailContent.contains("Temporary password: ch4l1Eng3Hm7C") }
            waitFor { emailContent.contains("You can access the forum from here: https://forum.hmrcloancharge.info/ Initially your ability to interact on the forum will be limited to the ‘Guest’ and ‘Welcome’ areas. This restriction will be lifted once we have verified your identity. In order to verify your identity we need to collect some additional information. If you are happy to proceed, please complete the LCAG membership form and we will get back to you as soon as we can: https://membership.hmrcloancharge.info?token=${getUserRows().get(0).membershipToken} Many thanks, LCAG FFC Team") }
            waitFor { getUserRows().get(0).getGroup() == "8" }
            waitFor { getUserRows().get(0).getAdditionalGroups() == "9,10" }
            GebTestUtils.verifyNoAttachments("harry@generous.com")

        when: "i navigate to the invoice page"
            go driver.currentUrl.replace("thankYou", "invoice")
            waitFor { at InvoicePage }

        then:
            verifyNoVatNumberInvoice(browser, "LCAGFFC90001", new Date(), "Card", "Harry Generous", "harry@generous.com", "Donation", "£250.00")

        when: "i set the vat number and refresh the page"
            runSqlUpdate("update i7b0_ffc_contributions set vat_number = '1234567890' where `reference` = 'LCAGFFC90001'")
            driver.navigate().refresh()
            waitFor { at InvoicePage }

        then:
            verifyInvoice(browser, "LCAGFFC90001", new Date(), "Card", "Harry Generous", "harry@generous.com", "Donation", "£250.00", "0%", "£0.00", "£250.00", "1234567890")
    }

    def "i can complete the payment flow for contribution agreement for a new lcag applicant"() {
        given:
            sleep(3000) //wait for member cache to be refreshed
            getUserRows().size() == 0
            go "http://localhost:8484"

        when:
            waitFor { at LcagFfcFormPage }

        then:
            verifyHappyInitialPaymentFormState(browser)

        when: "i agree to the t&cs"
            acceptTermsAndConditionsButton.click()

        then: "initial payment form questions appear and nothing is selected"
            verifyInitialPaymentFormQuestionsDisplayed(browser)

        when: "i click on the anonymous donation radio"
            existingLcagAccountNo.click()

        then: "credit card form is displayed"
            newLcagUserAccountPaymentCreditCardFormDisplayed(browser)

        when: "i enter valid lcag username value and payment details and click pay now"
            contributionTypeContributionAgreement.click()
            contributionAgreementAddressFieldsAreDisplayed(browser, true)
            enterContributionAgreementAddressDetails(browser, "Harry", "Generous", "harry@generous.com")
            amountInput = "2000.00"
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
            waitFor { emailContent.contains("Dear Harry Generous, Thank you for your contribution of £2,000.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. You have indicated that you would like to join LCAG and be kept up to date with the latest developments regarding the 2019 Loan Charge legal challenge. We have already set up a forum user account for you: Username: harry Temporary password:") }
            waitFor { emailContent.contains("Temporary password: 2019l0anCharg3") || emailContent.contains("Temporary password: lc4g2019Ch4rg3") || emailContent.contains("Temporary password: hm7cL04nch4rGe") || emailContent.contains("Temporary password: ch4l1Eng3Hm7C") }
            waitFor { emailContent.contains("You can access the forum from here: https://forum.hmrcloancharge.info/ Initially your ability to interact on the forum will be limited to the ‘Guest’ and ‘Welcome’ areas. This restriction will be lifted once we have verified your identity. In order to verify your identity we need to collect some additional information. If you are happy to proceed, please complete the LCAG membership form and we will get back to you as soon as we can: https://membership.hmrcloancharge.info?token=${getUserRows().get(0).membershipToken} Many thanks, LCAG FFC Team") }
            verifyAttachment("harry@generous.com", 0, 2, "lcag-ffc-payment-invoice-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf")
            verifyAttachment("harry@generous.com", 1, 2, "lcag-ffc-contribution-agreement-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf")
            waitFor { getUserRows().get(0).getGroup() == "8" }
            waitFor { getUserRows().get(0).getAdditionalGroups() == "9,10" }
            waitFor { getPaymentRows().size() == 1 }
            waitFor { getPaymentRows().get(0).id == 1L }
            waitFor { getPaymentRows().get(0).userId == 1L }
            waitFor { getPaymentRows().get(0).firstName == "Harry" }
            waitFor { getPaymentRows().get(0).lastName == "Generous" }
            waitFor { getPaymentRows().get(0).emailAddress == "harry@generous.com" }
            waitFor { getPaymentRows().get(0).addressLine1 == "10 Some Street" }
            waitFor { getPaymentRows().get(0).addressLine2 == "Some Village" }
            waitFor { getPaymentRows().get(0).city == "Some City" }
            waitFor { getPaymentRows().get(0).postalCode == "Some Postcode" }
            waitFor { getPaymentRows().get(0).country == "Some Country" }
            waitFor { getPaymentRows().get(0).reference == "LCAGFFC90001" }
            waitFor { getPaymentRows().get(0).contributionType == ContributionType.CONTRIBUTION_AGREEMENT }
            waitFor { getPaymentRows().get(0).paymentMethod == "Card" }

        when: "i navigate to the invoice page"
            go driver.currentUrl.replace("thankYou", "invoice")
            waitFor { at InvoicePage }

        then:
            verifyNoVatNumberInvoice(browser, "LCAGFFC90001", new Date(), "Card", "Harry Generous", "harry@generous.com", "Contribution Agreement", "£2,000.00")

        when: "i set the vat number and refresh the page"
            runSqlUpdate("update i7b0_ffc_contributions set vat_number = '1234567890' where `reference` = 'LCAGFFC90001'")
            driver.navigate().refresh()
            waitFor { at InvoicePage }

        then:
            verifyInvoice(browser, "LCAGFFC90001", new Date(), "Card", "Harry Generous", "harry@generous.com", "Contribution Agreement", "£1,666.67", "20%", "£333.33", "£2,000.00", "1234567890")
    }

}

