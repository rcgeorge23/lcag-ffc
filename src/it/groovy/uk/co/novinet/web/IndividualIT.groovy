package uk.co.novinet.web

import geb.spock.GebSpec
import uk.co.novinet.e2e.TestUtils
import uk.co.novinet.rest.PaymentType
import uk.co.novinet.service.ContributionType
import static uk.co.novinet.e2e.TestUtils.*
import static uk.co.novinet.web.GebTestUtils.*

import java.text.SimpleDateFormat

import static org.apache.commons.lang3.StringUtils.isBlank
class IndividualIT extends GebSpec {

    public static final String AUTHORIZED_CARD = "4242424242424242"
    public static final String DECLINED_CARD = "4000000000000002"

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

    def "i can complete the payment flow as an anonymous donor"() {
        given:
            GebTestUtils.driveToPaymentType(browser, "10", PaymentType.ANONYMOUS, ContributionType.DONATION)
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
            GebTestUtils.driveToPaymentType(browser, "10", PaymentType.ANONYMOUS, ContributionType.DONATION)
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
            GebTestUtils.driveToPaymentType(browser, "10", PaymentType.EXISTING_LCAG_MEMBER, ContributionType.DONATION)

        when: "i enter valid lcag username value and payment details and click pay now"
            username = "testuser1"
            contributionAgreementAddressFieldsAreDisplayed(browser, false)
            donationInfoSection.displayed == true
            contributionAgreementInfoSection.displayed == false
            enterCardDetails(browser, AUTHORIZED_CARD, "0222", "111", "33333")
            payNowButton.click()

        then: "i land on the thank you page and i receive a confirmation email"
            waitFor { at ThankYouPage }
            waitFor { paymentReference.text() == "LCAGFFC90001" }
            waitFor { getEmails("user1@something.com", "Inbox").size() == 1 }
            waitFor { getEmails("user1@something.com", "Inbox").get(0).content.contains("Dear Test Name1, Thank you for your payment of £10.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. Many thanks, LCAG FFC Team") }
            waitFor { getUserRows().get(0).getGroup() == "8" }
            assert isBlank(getUserRows().get(0).getAdditionalGroups())
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
            assert isBlank(getUserRows().get(0).getAdditionalGroups())
            GebTestUtils.driveToPaymentType(browser, "250", PaymentType.EXISTING_LCAG_MEMBER, ContributionType.DONATION)

        when: "i enter valid lcag username value and payment details and click pay now"
            username = "testuser1"
            contributionAgreementAddressFieldsAreDisplayed(browser, false)
            donationInfoSection.displayed == true
            contributionAgreementInfoSection.displayed == false
            enterCardDetails(browser, AUTHORIZED_CARD, "0222", "111", "33333")
            payNowButton.click()

        then: "i land on the thank you page and i receive a confirmation email"
            waitFor { at ThankYouPage }
            waitFor { paymentReference.text() == "LCAGFFC90001" }
            waitFor { getEmails("user1@something.com", "Inbox").size() == 1 }
            waitFor { getEmails("user1@something.com", "Inbox").get(0).content.contains("Dear Test Name1, Thank you for your payment of £250.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. Many thanks, LCAG FFC Team") }
            waitFor { getUserRows().get(0).getGroup() == "8" }
            assert isBlank(getUserRows().get(0).getAdditionalGroups())
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
            GebTestUtils.driveToPaymentType(browser, "600", PaymentType.EXISTING_LCAG_MEMBER, ContributionType.CONTRIBUTION_AGREEMENT)

        when: "i enter valid lcag username value and payment details and click pay now"
            username = "testuser1"
            contributionAgreementAddressFieldsAreDisplayed(browser, true)
            enterContributionAgreementAddressDetails(browser, "John", "Smith", "user1@something.com")
            enterCardDetails(browser, AUTHORIZED_CARD, "0222", "111", "33333")
            payNowButton.click()

        then: "i land on the thank you page and i receive a confirmation email"
            waitFor { at ThankYouPage }
            waitFor { paymentReference.text() == "LCAGFFC90001" }
            waitFor { getEmails("user1@something.com", "Inbox").size() == 1 }
            waitFor { getEmails("user1@something.com", "Inbox").get(0).content.contains("Dear John Smith, Thank you for your payment of £600.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. Many thanks, LCAG FFC Team") }
            waitFor { getUserRows().get(0).getGroup() == "8" }
            waitFor { getUserRows().get(0).getAdditionalGroups() == "9" }
            verifyAttachment("user1@something.com", 0, 2, "lcag-ffc-payment-invoice-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf")
            verifyAttachment("user1@something.com", 1, 2, "lcag-ffc-contribution-agreement-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf")

        when: "i navigate to the invoice page"
            go driver.currentUrl.replace("thankYou", "invoice")
            waitFor { at InvoicePage }

        then:
            verifyNoVatNumberInvoice(browser, "LCAGFFC90001", new Date(), "Card", "John Smith", "user1@something.com", "Contribution Agreement", "£600.00")

        when: "i set the vat number and refresh the page"
            runSqlUpdate("update i7b0_ffc_contributions set vat_number = '1234567890' where `reference` = 'LCAGFFC90001'")
            driver.navigate().refresh()
            waitFor { at InvoicePage }

        then:
            verifyInvoice(browser, "LCAGFFC90001", new Date(), "Card", "John Smith", "user1@something.com", "Contribution Agreement", "£500.00", "20%", "£100.00", "£600.00", "1234567890")

        when: "i navigate to the invoice page"
            go driver.currentUrl.replace("invoice", "contributionAgreement")
            waitFor { at ContributionAgreementPage }

        then:
            GebTestUtils.verifyContributionAgreement(browser, new Date(), "John Smith", "10 Some Street", "Some Village", "Some City", "Some Postcode", "Some Country", "£600.00")
    }

    def "payment declined for donation as an existing lcag member"() {
        given:
            insertUser(1, "testuser1", "user1@something.com", "Test Name1", 8, "1234_1", "claim_1")
            GebTestUtils.driveToPaymentType(browser, "10", PaymentType.EXISTING_LCAG_MEMBER, ContributionType.DONATION)

        when: "i enter valid lcag username value and payment details and click pay now"
            username = "testuser1"
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
            GebTestUtils.driveToPaymentType(browser, "200", PaymentType.NEW_LCAG_MEMBER, ContributionType.DONATION)

        when: "i enter valid lcag username value and payment details and click pay now"
            contributionAgreementAddressFieldsAreDisplayed(browser, false)
            firstNameInput = "Harry"
            lastNameInput = "Generous"
            emailAddressInput = "harry@generous.com"
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
            waitFor { emailContent.contains("Dear Harry Generous, Thank you for your donation of £200.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. You have indicated that you may like to join LCAG. We have already set up a forum user account for you: Username: harry Temporary password:") }
            waitFor { emailContent.contains("Temporary password: 2019l0anCharg3") || emailContent.contains("Temporary password: lc4g2019Ch4rg3") || emailContent.contains("Temporary password: hm7cL04nch4rGe") || emailContent.contains("Temporary password: ch4l1Eng3Hm7C") }
            waitFor { emailContent.contains("You can access the forum from here: https://forum.hmrcloancharge.info/ Initially your ability to interact on the forum will be limited to the ‘Guest’ and ‘Welcome’ areas. This restriction will be lifted if you become a full member. In order to become a full member we need to verify your identity and confirm receipt of your one-off £100 LCAG joining fee. If you are happy to proceed, please complete the LCAG membership form and we will get back to you as soon as we can: https://membership.hmrcloancharge.info?token=${getUserRows().get(0).membershipToken} Many thanks, LCAG FFC Team") }
            waitFor { getUserRows().get(0).getGroup() == "8" }
            assert isBlank(getUserRows().get(0).getAdditionalGroups())
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

    def "i can complete the payment flow for contribution agreement for a new lcag applicant"() {
        given:
            sleep(3000) //wait for member cache to be refreshed
            getUserRows().size() == 0
            GebTestUtils.driveToPaymentType(browser, "2000", PaymentType.NEW_LCAG_MEMBER, ContributionType.CONTRIBUTION_AGREEMENT)

        when: "i enter valid lcag username value and payment details and click pay now"
            contributionTypeContributionAgreement.click()
            contributionAgreementAddressFieldsAreDisplayed(browser, true)
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
            waitFor { emailContent.contains("Dear Harry Generous, Thank you for your contribution of £2,000.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. You have indicated that you would like to join LCAG and be kept up to date with the latest developments regarding the 2019 Loan Charge legal challenge. We have already set up a forum user account for you: Username: harry Temporary password:") }
            waitFor { emailContent.contains("Temporary password: 2019l0anCharg3") || emailContent.contains("Temporary password: lc4g2019Ch4rg3") || emailContent.contains("Temporary password: hm7cL04nch4rGe") || emailContent.contains("Temporary password: ch4l1Eng3Hm7C") }
            waitFor { emailContent.contains("You can access the forum from here: https://forum.hmrcloancharge.info/ Initially your ability to interact on the forum will be limited to the ‘Guest’, ‘Welcome’ and ‘LCAG FFC’ areas. This restriction will be lifted if you become a full member. In order to become a full member we need to verify your identity and confirm receipt of your one-off £100 LCAG joining fee. If you are happy to proceed, please complete the LCAG membership form and we will get back to you as soon as we can: https://membership.hmrcloancharge.info?token=${getUserRows().get(0).membershipToken} Many thanks, LCAG FFC Team") }
            verifyAttachment("harry@generous.com", 0, 2, "lcag-ffc-payment-invoice-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf")
            verifyAttachment("harry@generous.com", 1, 2, "lcag-ffc-contribution-agreement-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf")
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

    def "i can complete the payment flow for £2000 but choose donation instead of contribution agreement for a new lcag applicant"() {
        given:
            sleep(3000) //wait for member cache to be refreshed
            getUserRows().size() == 0
            GebTestUtils.driveToPaymentType(browser, "2000", PaymentType.NEW_LCAG_MEMBER, ContributionType.DONATION)

        when: "i enter valid lcag username value and payment details and click pay now"
            assert addressLine1Input.displayed == false
            assert addressLine2Input.displayed == false
            assert cityInput.displayed == false
            assert postalCodeInput.displayed == false
            assert countryInput.displayed == false
            assert contributionAgreementInfoSection.displayed == true
            assert donationInfoSection.displayed == false

            firstNameInput = "Harry"
            lastNameInput = "Generous"
            emailAddressInput = "harry@generous.com"
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
            waitFor { emailContent.contains("Dear Harry Generous, Thank you for your donation of £2,000.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. You have indicated that you may like to join LCAG. We have already set up a forum user account for you: Username: harry Temporary password:") }
            waitFor { emailContent.contains("Temporary password: 2019l0anCharg3") || emailContent.contains("Temporary password: lc4g2019Ch4rg3") || emailContent.contains("Temporary password: hm7cL04nch4rGe") || emailContent.contains("Temporary password: ch4l1Eng3Hm7C") }
            waitFor { emailContent.contains("You can access the forum from here: https://forum.hmrcloancharge.info/ Initially your ability to interact on the forum will be limited to the ‘Guest’ and ‘Welcome’ areas. This restriction will be lifted if you become a full member. In order to become a full member we need to verify your identity and confirm receipt of your one-off £100 LCAG joining fee. If you are happy to proceed, please complete the LCAG membership form and we will get back to you as soon as we can: https://membership.hmrcloancharge.info?token=${getUserRows().get(0).membershipToken} Many thanks, LCAG FFC Team") }
            verifyNoAttachments("harry@generous.com")
            waitFor { getUserRows().get(0).getGroup() == "8" }
            assert isBlank(getUserRows().get(0).getAdditionalGroups())
            waitFor { getPaymentRows().size() == 1 }
            waitFor { getPaymentRows().get(0).id == 1L }
            waitFor { getPaymentRows().get(0).userId == 1L }
            waitFor { getPaymentRows().get(0).firstName == "Harry" }
            waitFor { getPaymentRows().get(0).lastName == "Generous" }
            waitFor { getPaymentRows().get(0).emailAddress == "harry@generous.com" }
            waitFor { getPaymentRows().get(0).reference == "LCAGFFC90001" }
            waitFor { getPaymentRows().get(0).contributionType == ContributionType.DONATION }
            waitFor { getPaymentRows().get(0).paymentMethod == "Card" }

        when: "i navigate to the invoice page"
            go driver.currentUrl.replace("thankYou", "invoice")
            waitFor { at InvoicePage }

        then:
            verifyNoVatNumberInvoice(browser, "LCAGFFC90001", new Date(), "Card", "Harry Generous", "harry@generous.com", "Donation", "£2,000.00")

        when: "i set the vat number and refresh the page"
            runSqlUpdate("update i7b0_ffc_contributions set vat_number = '1234567890' where `reference` = 'LCAGFFC90001'")
            driver.navigate().refresh()
            waitFor { at InvoicePage }

        then:
            verifyInvoice(browser, "LCAGFFC90001", new Date(), "Card", "Harry Generous", "harry@generous.com", "Donation", "£2,000.00", "0%", "£0.00", "£2,000.00", "1234567890")
    }

    def "i can complete the payment flow for £2000 but choose donation instead of contribution agreement for an existing lcag member"() {
        given:
            insertUser(1, "testuser1", "user1@something.com", "Test Name1", 8, "1234_1", "claim_1")
            sleep(3000) //wait for member cache to be refreshed
            getUserRows().size() == 0
            GebTestUtils.driveToPaymentType(browser, "2000", PaymentType.EXISTING_LCAG_MEMBER, ContributionType.DONATION)

        when: "i enter valid lcag username value and payment details and click pay now"
            assert firstNameInput.displayed == false
            assert lastNameInput.displayed == false
            assert emailAddressInput.displayed == false
            assert addressLine1Input.displayed == false
            assert addressLine2Input.displayed == false
            assert cityInput.displayed == false
            assert postalCodeInput.displayed == false
            assert countryInput.displayed == false
            assert contributionAgreementInfoSection.displayed == true
            assert donationInfoSection.displayed == false

            username = "testuser1"

            enterCardDetails(browser, AUTHORIZED_CARD, "0222", "111", "33333")

            payNowButton.click()

            waitFor { getEmails("user1@something.com", "Inbox").size() == 1 }
            String emailContent = getEmails("user1@something.com", "Inbox").get(0).content

        then: "i land on the thank you page and i receive a confirmation email"
            waitFor { at ThankYouPage }
            waitFor { paymentReference.text() == "LCAGFFC90001" }
            waitFor { getUserRows().size() == 1 }
            waitFor { getUserRows().get(0).emailAddress == "user1@something.com" }
            waitFor { getUserRows().get(0).name == "Test Name1" }
            waitFor { getEmails("user1@something.com", "Inbox").size() == 1 }
            waitFor { emailContent.contains("Dear Test Name1, Thank you for your payment of £2,000.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. Many thanks, LCAG FFC Team") }
            verifyNoAttachments("user1@something.com")
            waitFor { getUserRows().get(0).getGroup() == "8" }
            assert isBlank(getUserRows().get(0).getAdditionalGroups())
            waitFor { getPaymentRows().size() == 1 }
            waitFor { getPaymentRows().get(0).id == 1L }
            waitFor { getPaymentRows().get(0).userId == 1L }
            waitFor { getPaymentRows().get(0).firstName == "Test" }
            waitFor { getPaymentRows().get(0).lastName == "Name1" }
            waitFor { getPaymentRows().get(0).emailAddress == "user1@something.com" }
            waitFor { getPaymentRows().get(0).reference == "LCAGFFC90001" }
            waitFor { getPaymentRows().get(0).contributionType == ContributionType.DONATION }
            waitFor { getPaymentRows().get(0).paymentMethod == "Card" }

        when: "i navigate to the invoice page"
            go driver.currentUrl.replace("thankYou", "invoice")
            waitFor { at InvoicePage }

        then:
            verifyNoVatNumberInvoice(browser, "LCAGFFC90001", new Date(), "Card", "Test Name1", "user1@something.com", "Donation", "£2,000.00")

        when: "i set the vat number and refresh the page"
            runSqlUpdate("update i7b0_ffc_contributions set vat_number = '1234567890' where `reference` = 'LCAGFFC90001'")
            driver.navigate().refresh()
            waitFor { at InvoicePage }

        then:
            verifyInvoice(browser, "LCAGFFC90001", new Date(), "Card", "Test Name1", "user1@something.com", "Donation", "£2,000.00", "0%", "£0.00", "£2,000.00", "1234567890")
    }



    def "i can make 2 donations of £300 each as an existing lcag member"() {
        given:
            insertUser(1, "testuser1", "user1@something.com", "Test Name1", 8, "1234_1", "claim_1")
            GebTestUtils.driveToPaymentType(browser, "300", PaymentType.EXISTING_LCAG_MEMBER, ContributionType.DONATION)

        when: "i enter valid lcag username value and payment details and click pay now"
            username = "testuser1"
            contributionAgreementAddressFieldsAreDisplayed(browser, false)
            donationInfoSection.displayed == true
            contributionAgreementInfoSection.displayed == false
            enterCardDetails(browser, AUTHORIZED_CARD, "0222", "111", "33333")
            payNowButton.click()

        then: "i land on the thank you page and i receive a confirmation email"
            waitFor { at ThankYouPage }
            waitFor { paymentReference.text() == "LCAGFFC90001" }
            waitFor { getEmails("user1@something.com", "Inbox").size() == 1 }
            waitFor { getEmails("user1@something.com", "Inbox").get(0).content.contains("Dear Test Name1, Thank you for your payment of £300.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. Many thanks, LCAG FFC Team") }
            waitFor { getUserRows().get(0).getGroup() == "8" }
            assert isBlank(getUserRows().get(0).getAdditionalGroups())
            GebTestUtils.verifyNoAttachments("user1@something.com")

        when:
            GebTestUtils.driveToPaymentType(browser, "300", PaymentType.EXISTING_LCAG_MEMBER, ContributionType.DONATION)
            username = "testuser1"
            contributionAgreementAddressFieldsAreDisplayed(browser, false)
            donationInfoSection.displayed == true
            contributionAgreementInfoSection.displayed == false
            enterCardDetails(browser, AUTHORIZED_CARD, "0222", "111", "33333")
            payNowButton.click()

        then: "i land on the thank you page and i receive a confirmation email"
            waitFor { at ThankYouPage }
            waitFor { paymentReference.text() == "LCAGFFC90002" }
            waitFor { getEmails("user1@something.com", "Inbox").size() == 2 }
            waitFor { getEmails("user1@something.com", "Inbox").get(1).content.contains("Dear Test Name1, Thank you for your donation of £300.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90002. Many thanks, LCAG FFC Team") }
            waitFor { getUserRows().get(0).getGroup() == "8" }
            assert isBlank(getUserRows().get(0).getAdditionalGroups())
            GebTestUtils.verifyNoAttachments("user1@something.com")

    }

    def "second contribution of less than £600 does not remove my fcc forum group membership"() {
        given:
            sleep(3000) //wait for member cache to be refreshed
            getUserRows().size() == 0
            GebTestUtils.driveToPaymentType(browser, "2000", PaymentType.NEW_LCAG_MEMBER, ContributionType.CONTRIBUTION_AGREEMENT)

        when: "i enter valid lcag username value and payment details and click pay now"
            contributionTypeContributionAgreement.click()
            contributionAgreementAddressFieldsAreDisplayed(browser, true)
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
            waitFor { emailContent.contains("Dear Harry Generous, Thank you for your contribution of £2,000.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90001. You have indicated that you would like to join LCAG and be kept up to date with the latest developments regarding the 2019 Loan Charge legal challenge. We have already set up a forum user account for you: Username: harry Temporary password:") }
            waitFor { emailContent.contains("Temporary password: 2019l0anCharg3") || emailContent.contains("Temporary password: lc4g2019Ch4rg3") || emailContent.contains("Temporary password: hm7cL04nch4rGe") || emailContent.contains("Temporary password: ch4l1Eng3Hm7C") }
            waitFor { emailContent.contains("You can access the forum from here: https://forum.hmrcloancharge.info/ Initially your ability to interact on the forum will be limited to the ‘Guest’, ‘Welcome’ and ‘LCAG FFC’ areas. This restriction will be lifted if you become a full member. In order to become a full member we need to verify your identity and confirm receipt of your one-off £100 LCAG joining fee. If you are happy to proceed, please complete the LCAG membership form and we will get back to you as soon as we can: https://membership.hmrcloancharge.info?token=${getUserRows().get(0).membershipToken} Many thanks, LCAG FFC Team") }

        when:
            GebTestUtils.driveToPaymentType(browser, "300", PaymentType.EXISTING_LCAG_MEMBER, ContributionType.DONATION)
            username = "harry"
            contributionAgreementAddressFieldsAreDisplayed(browser, false)
            donationInfoSection.displayed == true
            contributionAgreementInfoSection.displayed == false
            enterCardDetails(browser, AUTHORIZED_CARD, "0222", "111", "33333")
            payNowButton.click()

        then: "i land on the thank you page and i receive a confirmation email"
            waitFor { at ThankYouPage }
            waitFor { paymentReference.text() == "LCAGFFC90002" }
            waitFor { getEmails("harry@generous.com", "Inbox").size() == 2 }
            waitFor { getEmails("harry@generous.com", "Inbox").get(1).content.contains("Dear Harry Generous, Thank you for your payment of £300.00 towards the Loan Charge Action Group litigation fund. Your payment reference is LCAGFFC90002. Many thanks, LCAG FFC Team") }
            waitFor { getUserRows().get(0).getGroup() == "8" }
            waitFor { getUserRows().get(0).getAdditionalGroups() == "9" }
            GebTestUtils.verifyNoAttachments("harry@generous.com", 1)
            getPaymentRows().size() == 2
            getPaymentRows()[0].username == "harry"
            getPaymentRows()[1].username == "harry"
            getPaymentRows()[0].grossAmount == new BigDecimal("2000.00")
            getPaymentRows()[1].grossAmount == new BigDecimal("300.00")
    }

}

