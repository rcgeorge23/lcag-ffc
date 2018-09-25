package uk.co.novinet.web

import geb.spock.GebSpec
import uk.co.novinet.e2e.TestUtils
import uk.co.novinet.rest.PaymentType

import static uk.co.novinet.e2e.TestUtils.*
import static uk.co.novinet.web.GebTestUtils.*

import java.text.SimpleDateFormat

import static org.apache.commons.lang3.StringUtils.isBlank
class IndividualIT extends GebSpec {

    public static final String AUTHORIZED_CARD = "4242424242424242"
    public static final String DECLINED_CARD = "4000000000000002"

    /*
        Latest Todos:
        =============

        New docs: https://drive.google.com/drive/u/0/folders/1L5quy5hp6jd0URcmps0WjiC3sh60wOG-

        * We need to send a copy of the trust deed to everyone
        * Contribution Agreement wording has been updated - the bit about BACS should be displayed dynamically. If it's a card payment, show the card payment bit, otherwise the BACS bit if payment type is BACS
        * We need to capture an electronic signature
        * We should store a copy of the signed Contribution Agreement
        * We should send the user a copy of the signed Contribution Agreement

        * No donations - everyone needs to sign a contribution agreement - DONE
        * We are going to be VAT exempt - we don't need to charge VAT for anyone - DONE
        * What was our T&Cs has now been renamed to Contributor Guidance Notes - please confirm you have read and understood the Contributor Guidance Notes - DONE
     */

    //TESTS TO ADD:
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

    def "i can complete the payment flow for contribution agreement as an existing lcag member"() {
        given:
            insertUser(1, "testuser1", "user1@something.com", "Test Name1", 8, "1234_1", "claim_1")
            isBlank(getUserRows().get(0).getAdditionalGroups())
            GebTestUtils.driveToPaymentType(browser, "600", PaymentType.EXISTING_LCAG_MEMBER)

        when: "i enter valid lcag username value and payment details and click pay now"
            username = "testuser1"
            contributionAgreementAddressFieldsAreDisplayed(browser, true, 600)
            enterContributionAgreementAddressDetails(browser, "John", "Smith", "user1@something.com")
            enterCardDetails(browser, AUTHORIZED_CARD, "0222", "111", "33333")
            payNowButton.click()

        then: "i land on the thank you page and i receive a confirmation email"
            waitFor(10) { at ThankYouPage }
            waitFor { paymentReference.text() == "LCAGFFC90001" }
            waitFor { getEmails("user1@something.com", "Inbox").size() == 1 }
            waitFor { getEmails("user1@something.com", "Inbox").get(0).content.contains("Dear John Smith, Thank you for your contribution of £600.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. A contribution agreement document will be sent to you by email in due course. Many thanks, LCAG FFC Team") }
            waitFor { getUserRows().get(0).getGroup() == "8" }
            waitFor { getUserRows().get(0).getAdditionalGroups() == "9" }
//            verifyAttachment("user1@something.com", 0, 3, "lcag-ffc-payment-invoice-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf")
//            verifyAttachment("user1@something.com", 1, 3, "lcag-ffc-contribution-agreement-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf")
//            verifyAttachment("user1@something.com", 2, 3, "lcag-ffc-guidance-notes.pdf")
        verifyAttachment("user1@something.com", 0, 2, "lcag-ffc-payment-invoice-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf")
        verifyAttachment("user1@something.com", 1, 2, "lcag-ffc-guidance-notes.pdf")

        when: "i navigate to the invoice page"
            go driver.currentUrl.replace("thankYou", "invoice")
            waitFor { at InvoicePage }

        then:
            verifyNoVatNumberInvoice(browser, "LCAGFFC90001", new Date(), "Card", "John Smith", "user1@something.com", "£600.00")

        when: "i navigate to the contribution agreement page"
            go driver.currentUrl.replace("invoice", "contributionAgreement")
            waitFor { at ContributionAgreementPage }

        then:
            GebTestUtils.verifyContributionAgreement(browser, new Date(), "John Smith", "10 Some Street", "Some Village", "Some City", "Some Postcode", "Some Country", "£600.00")
    }

    def "i can complete the payment flow for contribution agreement saying i want to create a new lcag account, but my email address already exists"() {
        given:
            insertUser(1, "testuser1", "user1@something.com", "Test Name1", 8, "1234_1", "claim_1")
            isBlank(getUserRows().get(0).getAdditionalGroups())
            GebTestUtils.driveToPaymentType(browser, "600", PaymentType.NEW_LCAG_MEMBER)

        when: "i enter valid lcag username value and payment details and click pay now"
            contributionAgreementAddressFieldsAreDisplayed(browser, true, 600)
            enterContributionAgreementAddressDetails(browser, "John", "Smith", "user1@something.com")
            enterCardDetails(browser, AUTHORIZED_CARD, "0222", "111", "33333")
            payNowButton.click()

        then: "i land on the thank you page and i receive a confirmation email"
            waitFor { at ThankYouPage }
            waitFor { paymentReference.text() == "LCAGFFC90001" }
            waitFor { getEmails("user1@something.com", "Inbox").size() == 1 }
            waitFor { getEmails("user1@something.com", "Inbox").get(0).content.contains("Dear John Smith, Thank you for your contribution of £600.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. A contribution agreement document will be sent to you by email in due course. Many thanks, LCAG FFC Team") }
            waitFor { getUserRows().get(0).getGroup() == "8" }
            waitFor { getUserRows().get(0).getAdditionalGroups() == "9" }
//            verifyAttachment("user1@something.com", 0, 3, "lcag-ffc-payment-invoice-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf")
//            verifyAttachment("user1@something.com", 1, 3, "lcag-ffc-contribution-agreement-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf")
//            verifyAttachment("user1@something.com", 2, 3, "lcag-ffc-guidance-notes.pdf")
            verifyAttachment("user1@something.com", 0, 2, "lcag-ffc-payment-invoice-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf")
            verifyAttachment("user1@something.com", 1, 2, "lcag-ffc-guidance-notes.pdf")

        when: "i navigate to the invoice page"
            go driver.currentUrl.replace("thankYou", "invoice")
            waitFor { at InvoicePage }

        then:
            verifyNoVatNumberInvoice(browser, "LCAGFFC90001", new Date(), "Card", "John Smith", "user1@something.com", "£600.00")

        when: "i navigate to the contribution agreement page"
            go driver.currentUrl.replace("invoice", "contributionAgreement")
            waitFor { at ContributionAgreementPage }

        then:
            GebTestUtils.verifyContributionAgreement(browser, new Date(), "John Smith", "10 Some Street", "Some Village", "Some City", "Some Postcode", "Some Country", "£600.00")
    }

    def "payment declined as an existing lcag member"() {
        given:
            insertUser(1, "testuser1", "user1@something.com", "Test Name1", 8, "1234_1", "claim_1")
            GebTestUtils.driveToPaymentType(browser, "10", PaymentType.EXISTING_LCAG_MEMBER)

        when: "i enter valid lcag username value and payment details and click pay now"
            username = "testuser1"
            contributionAgreementAddressFieldsAreDisplayed(browser, true, 10)
            enterContributionAgreementAddressDetails(browser, "John", "Smith", "user1@something.com")
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

    def "i can complete the payment flow for a new lcag applicant for less than £250"() {
        given:
            sleep(3000) //wait for member cache to be refreshed
            getUserRows().size() == 0
            GebTestUtils.driveToPaymentType(browser, "200", PaymentType.NEW_LCAG_MEMBER)

        when: "i enter valid lcag username value and payment details and click pay now"
            contributionAgreementAddressFieldsAreDisplayed(browser, true, 200)
            enterContributionAgreementAddressDetails(browser, "Harry", "Generous", "harry@generous.com")
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

            waitFor { emailContent.contains("Dear Harry Generous, Thank you for your contribution of £200.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. A contribution agreement document will be sent to you by email in due course. You have indicated that you would like to join LCAG and be kept up to date with the latest developments regarding the 2019 Loan Charge legal challenge. We have already set up a forum user account for you: Username: harry Temporary password:") }
            waitFor { emailContent.contains("Temporary password: 2019l0anCharg3") || emailContent.contains("Temporary password: lc4g2019Ch4rg3") || emailContent.contains("Temporary password: hm7cL04nch4rGe") || emailContent.contains("Temporary password: ch4l1Eng3Hm7C") }
            waitFor { emailContent.contains("You can access the forum from here: https://forum.hmrcloancharge.info/ Initially your ability to interact on the forum will be limited to the ‘Guest’, ‘Welcome’ and ‘LCAG FFC’ areas. This restriction will be lifted if you become a full member. In order to become a full member we need to verify your identity and confirm receipt of your one-off £100 LCAG joining fee. If you are happy to proceed, please complete the LCAG membership form and we will get back to you as soon as we can: https://membership.hmrcloancharge.info?token=${getUserRows().get(0).membershipToken} Many thanks, LCAG FFC Team") }
            waitFor { getUserRows().get(0).getGroup() == "8" }
            waitFor { getUserRows().get(0).getAdditionalGroups() == "9" }
            verifyAttachment("harry@generous.com", 0, 2, "lcag-ffc-payment-invoice-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf")
            verifyAttachment("harry@generous.com", 1, 2, "lcag-ffc-guidance-notes.pdf")

        when: "i navigate to the invoice page"
            go driver.currentUrl.replace("thankYou", "invoice")
            waitFor { at InvoicePage }

        then:
            verifyNoVatNumberInvoice(browser, "LCAGFFC90001", new Date(), "Card", "Harry Generous", "harry@generous.com", "£200.00")
    }

    def "new lcag member - declined card, then use the same email address to create an account for a new lcag member with a valid card"() {
        given:
            sleep(3000) //wait for member cache to be refreshed
            getUserRows().size() == 0
            GebTestUtils.driveToPaymentType(browser, "200", PaymentType.NEW_LCAG_MEMBER)

        when: "i enter valid lcag username value and payment details and click pay now"
            contributionAgreementAddressFieldsAreDisplayed(browser, true, 200)
            enterContributionAgreementAddressDetails(browser, "Harry", "Generous", "harry@generous.com")
            enterCardDetails(browser, DECLINED_CARD, "0222", "111", "33333")
            payNowButton.click()

        then:
            waitFor { at LcagFfcFormPage }
            waitFor { paymentDeclinedSection.displayed == true }
            sleep(3000) //wait for member cache to be refreshed

        when:
            GebTestUtils.driveToPaymentType(browser, "200", PaymentType.NEW_LCAG_MEMBER)
            contributionAgreementAddressFieldsAreDisplayed(browser, true, 200)
            enterContributionAgreementAddressDetails(browser, "Harry", "Generous", "harry@generous.com")
            firstNameInput = "Harry"
            lastNameInput = "Generous"
            emailAddressInput = "harry@generous.com"
            enterCardDetails(browser, AUTHORIZED_CARD, "0222", "111", "33333")
            payNowButton.click()

            waitFor { at ThankYouPage }
            waitFor { getEmails("harry@generous.com", "Inbox").size() == 1 }
            String emailContent = getEmails("harry@generous.com", "Inbox").get(0).content

        and: "i land on the thank you page and i receive a confirmation email"
            waitFor { at ThankYouPage }
            waitFor { paymentReference.text() == "LCAGFFC90002" }
            waitFor { getUserRows().size() == 2 }
            waitFor { getUserRows().get(0).emailAddress.startsWith("DELETED_ON_") && getUserRows().get(0).emailAddress.endsWith("harry@generous.com") }
            waitFor { getUserRows().get(0).username.startsWith("DELETED_ON_")  && getUserRows().get(0).username.endsWith("harry") }
            waitFor { getUserRows().get(0).name == "Harry Generous" }
            waitFor { getUserRows().get(1).emailAddress == "harry@generous.com" }
            waitFor { getUserRows().get(1).username == "harry" }
            waitFor { getUserRows().get(1).name == "Harry Generous" }
            waitFor { getEmails("harry@generous.com", "Inbox").size() == 1 }
            waitFor { emailContent.contains("Dear Harry Generous, Thank you for your contribution of £200.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90002. A contribution agreement document will be sent to you by email in due course. You have indicated that you would like to join LCAG and be kept up to date with the latest developments regarding the 2019 Loan Charge legal challenge. We have already set up a forum user account for you: Username: harry Temporary password:") }
            waitFor { emailContent.contains("Temporary password: 2019l0anCharg3") || emailContent.contains("Temporary password: lc4g2019Ch4rg3") || emailContent.contains("Temporary password: hm7cL04nch4rGe") || emailContent.contains("Temporary password: ch4l1Eng3Hm7C") }
            waitFor { emailContent.contains("You can access the forum from here: https://forum.hmrcloancharge.info/ Initially your ability to interact on the forum will be limited to the ‘Guest’, ‘Welcome’ and ‘LCAG FFC’ areas. This restriction will be lifted if you become a full member. In order to become a full member we need to verify your identity and confirm receipt of your one-off £100 LCAG joining fee. If you are happy to proceed, please complete the LCAG membership form and we will get back to you as soon as we can: https://membership.hmrcloancharge.info?token=${getUserRows().get(1).membershipToken} Many thanks, LCAG FFC Team") }
            waitFor { getUserRows().get(0).getGroup() == "8" }
            waitFor { getUserRows().get(0).getAdditionalGroups() == "9" }
            verifyAttachment("harry@generous.com", 0, 2, "lcag-ffc-payment-invoice-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf")
            verifyAttachment("harry@generous.com", 1, 2, "lcag-ffc-guidance-notes.pdf")
            go driver.currentUrl.replace("thankYou", "invoice")
            waitFor { at InvoicePage }

        then:
            verifyNoVatNumberInvoice(browser, "LCAGFFC90002", new Date(), "Card", "Harry Generous", "harry@generous.com", "£200.00")
    }

    def "i can complete the payment flow for contribution agreement for a new lcag applicant"() {
        given:
            sleep(3000) //wait for member cache to be refreshed
            getUserRows().size() == 0
            GebTestUtils.driveToPaymentType(browser, "2000", PaymentType.NEW_LCAG_MEMBER)

        when: "i enter valid lcag username value and payment details and click pay now"
            contributionAgreementAddressFieldsAreDisplayed(browser, true, 2000)
            enterContributionAgreementAddressDetails(browser, "Harry", "Generous", "harry@generous.com")
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
            waitFor { emailContent.contains("Dear Harry Generous, Thank you for your contribution of £2,000.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. A contribution agreement document will be sent to you by email in due course. You have indicated that you would like to join LCAG and be kept up to date with the latest developments regarding the 2019 Loan Charge legal challenge. We have already set up a forum user account for you: Username: harry Temporary password:") }
            waitFor { emailContent.contains("Temporary password: 2019l0anCharg3") || emailContent.contains("Temporary password: lc4g2019Ch4rg3") || emailContent.contains("Temporary password: hm7cL04nch4rGe") || emailContent.contains("Temporary password: ch4l1Eng3Hm7C") }
            waitFor { emailContent.contains("You can access the forum from here: https://forum.hmrcloancharge.info/ Initially your ability to interact on the forum will be limited to the ‘Guest’, ‘Welcome’ and ‘LCAG FFC’ areas. This restriction will be lifted if you become a full member. In order to become a full member we need to verify your identity and confirm receipt of your one-off £100 LCAG joining fee. If you are happy to proceed, please complete the LCAG membership form and we will get back to you as soon as we can: https://membership.hmrcloancharge.info?token=${getUserRows().get(0).membershipToken} Many thanks, LCAG FFC Team") }
            verifyAttachment("harry@generous.com", 0, 2, "lcag-ffc-payment-invoice-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf")
            verifyAttachment("harry@generous.com", 1, 2, "lcag-ffc-guidance-notes.pdf")
            waitFor { getUserRows().get(0).getGroup() == "8" }
            waitFor { getUserRows().get(0).getAdditionalGroups() == "9" }
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
            waitFor { getPaymentRows().get(0).paymentMethod == "Card" }

        when: "i navigate to the invoice page"
            go driver.currentUrl.replace("thankYou", "invoice")
            waitFor { at InvoicePage }

        then:
            verifyNoVatNumberInvoice(browser, "LCAGFFC90001", new Date(), "Card", "Harry Generous", "harry@generous.com", "£2,000.00")
    }

    def "i can make 2 donations of £300 each as an existing lcag member"() {
        given:
            insertUser(1, "testuser1", "user1@something.com", "Test Name1", 8, "1234_1", "claim_1")
            GebTestUtils.driveToPaymentType(browser, "300", PaymentType.EXISTING_LCAG_MEMBER)

        when: "i enter valid lcag username value and payment details and click pay now"
            username = "testuser1"
            contributionAgreementAddressFieldsAreDisplayed(browser, true, 300)
            enterContributionAgreementAddressDetails(browser, "Test", "Name1", "user1@something.com")
            enterCardDetails(browser, AUTHORIZED_CARD, "0222", "111", "33333")
            payNowButton.click()

        then: "i land on the thank you page and i receive a confirmation email"
            waitFor { at ThankYouPage }
            waitFor { paymentReference.text() == "LCAGFFC90001" }
            waitFor { getEmails("user1@something.com", "Inbox").size() == 1 }
            waitFor { getEmails("user1@something.com", "Inbox").get(0).content.contains("Dear Test Name1, Thank you for your contribution of £300.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. A contribution agreement document will be sent to you by email in due course. Many thanks, LCAG FFC Team") }
            waitFor { getUserRows().get(0).getGroup() == "8" }
            waitFor { getUserRows().get(0).getAdditionalGroups() == "9" }
            verifyAttachment("user1@something.com", 0, 2, "lcag-ffc-payment-invoice-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf")
            verifyAttachment("user1@something.com", 1, 2, "lcag-ffc-guidance-notes.pdf")

        when:
            GebTestUtils.driveToPaymentType(browser, "300", PaymentType.EXISTING_LCAG_MEMBER)
            username = "testuser1"
            contributionAgreementAddressFieldsAreDisplayed(browser, true, 300)
            enterContributionAgreementAddressDetails(browser, "Test", "Name1", "user1@something.com")
            enterCardDetails(browser, AUTHORIZED_CARD, "0222", "111", "33333")
            payNowButton.click()

        then: "i land on the thank you page and i receive a confirmation email"
            waitFor { at ThankYouPage }
            waitFor { paymentReference.text() == "LCAGFFC90002" }
            waitFor { getEmails("user1@something.com", "Inbox").size() == 2 }
            waitFor { getEmails("user1@something.com", "Inbox").get(1).content.contains("Dear Test Name1, Thank you for your contribution of £300.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90002. A contribution agreement document will be sent to you by email in due course. Many thanks, LCAG FFC Team") }
            waitFor { getUserRows().get(0).getGroup() == "8" }
            waitFor { getUserRows().get(0).getAdditionalGroups() == "9" }
            verifyAttachment("user1@something.com", 0, 2, "lcag-ffc-payment-invoice-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf", 1)
            verifyAttachment("user1@something.com", 1, 2, "lcag-ffc-guidance-notes.pdf", 1)

    }

    def "second contribution of less than £600 does not remove my fcc forum group membership"() {
        given:
            sleep(3000) //wait for member cache to be refreshed
            getUserRows().size() == 0
            GebTestUtils.driveToPaymentType(browser, "2000", PaymentType.NEW_LCAG_MEMBER)

        when: "i enter valid lcag username value and payment details and click pay now"
            contributionAgreementAddressFieldsAreDisplayed(browser, true, 2000)
            enterContributionAgreementAddressDetails(browser, "Harry", "Generous", "harry@generous.com")
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
            waitFor { getUserRows().get(0).getGroup() == "8" }
            waitFor { getUserRows().get(0).getAdditionalGroups() == "9" }
            waitFor { getEmails("harry@generous.com", "Inbox").size() == 1 }
//            verifyAttachment("harry@generous.com", 0, 3, "lcag-ffc-payment-invoice-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf")
//            verifyAttachment("harry@generous.com", 1, 3, "lcag-ffc-contribution-agreement-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf")
//            verifyAttachment("harry@generous.com", 2, 3, "lcag-ffc-guidance-notes.pdf")
            verifyAttachment("harry@generous.com", 0, 2, "lcag-ffc-payment-invoice-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf")
            verifyAttachment("harry@generous.com", 1, 2, "lcag-ffc-guidance-notes.pdf")
            waitFor { emailContent.contains("Dear Harry Generous, Thank you for your contribution of £2,000.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. A contribution agreement document will be sent to you by email in due course. You have indicated that you would like to join LCAG and be kept up to date with the latest developments regarding the 2019 Loan Charge legal challenge. We have already set up a forum user account for you: Username: harry Temporary password:") }
            waitFor { emailContent.contains("Temporary password: 2019l0anCharg3") || emailContent.contains("Temporary password: lc4g2019Ch4rg3") || emailContent.contains("Temporary password: hm7cL04nch4rGe") || emailContent.contains("Temporary password: ch4l1Eng3Hm7C") }
            waitFor { emailContent.contains("You can access the forum from here: https://forum.hmrcloancharge.info/ Initially your ability to interact on the forum will be limited to the ‘Guest’, ‘Welcome’ and ‘LCAG FFC’ areas. This restriction will be lifted if you become a full member. In order to become a full member we need to verify your identity and confirm receipt of your one-off £100 LCAG joining fee. If you are happy to proceed, please complete the LCAG membership form and we will get back to you as soon as we can: https://membership.hmrcloancharge.info?token=${getUserRows().get(0).membershipToken} Many thanks, LCAG FFC Team") }

        when:
            GebTestUtils.driveToPaymentType(browser, "300", PaymentType.EXISTING_LCAG_MEMBER)
            username = "harry"
            contributionAgreementAddressFieldsAreDisplayed(browser, true, 300)
            enterContributionAgreementAddressDetails(browser, "Harry", "Generous", "harry@generous.com")
            enterCardDetails(browser, AUTHORIZED_CARD, "0222", "111", "33333")
            payNowButton.click()

        then: "i land on the thank you page and i receive a confirmation email"
            waitFor { at ThankYouPage }
            waitFor { paymentReference.text() == "LCAGFFC90002" }
            waitFor { getEmails("harry@generous.com", "Inbox").size() == 2 }
            waitFor { getEmails("harry@generous.com", "Inbox").get(1).content.contains("Dear Harry Generous, Thank you for your contribution of £300.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90002. A contribution agreement document will be sent to you by email in due course. Many thanks, LCAG FFC Team") }
            waitFor { getUserRows().get(0).getGroup() == "8" }
            waitFor { getUserRows().get(0).getAdditionalGroups() == "9" }
            verifyAttachment("harry@generous.com", 0, 2, "lcag-ffc-payment-invoice-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf")
            verifyAttachment("harry@generous.com", 1, 2, "lcag-ffc-guidance-notes.pdf")
            getPaymentRows().size() == 2
            getPaymentRows()[0].username == "harry"
            getPaymentRows()[1].username == "harry"
            getPaymentRows()[0].grossAmount == new BigDecimal("2000.00")
            getPaymentRows()[1].grossAmount == new BigDecimal("300.00")
    }

}

