package uk.co.novinet.web;

import geb.spock.GebSpec
import uk.co.novinet.e2e.TestUtils;
import uk.co.novinet.rest.PaymentType
import uk.co.novinet.service.ContributionType;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static uk.co.novinet.e2e.TestUtils.*;
import static uk.co.novinet.web.GebTestUtils.*;

class VatRegisteredCorporationIT extends GebSpec {

    public static final String AUTHORIZED_CARD = "4242424242424242"
    public static final String DECLINED_CARD = "4000000000000002"

    def setup() {
        TestUtils.setupDatabaseSchema()

        deleteAllMessages("user1@something.com")
        deleteAllMessages("harry@generous.com")
        getEmails("user1@something.com", "Inbox").size() == 0
        getEmails("harry@generous.com", "Inbox").size() == 0
    }

    def "i can complete the payment flow for contribution agreement as an existing lcag member, on behalf of a VAT registered company"() {
        given:
            insertUser(1, "testuser1", "user1@something.com", "Test Name1", 8, "1234_1", "claim_1")
            isBlank(getUserRows().get(0).getAdditionalGroups())
            GebTestUtils.driveToPaymentType(browser, PaymentType.EXISTING_LCAG_MEMBER, ContributionType.CONTRIBUTION_AGREEMENT, true)

        when: "i enter valid lcag username value and payment details and click pay now"
            username = "testuser1"
            companyNameInput = "My Company"
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
            verifyInvoice(browser, "LCAGFFC90001", new Date(), "Card", "John Smith", "user1@something.com", "Contribution Agreement", "£833.33", "20%", "£166.67", "£1,000.00", "1234567890", "My Company")

        when: "i navigate to the invoice page"
            go driver.currentUrl.replace("invoice", "contributionAgreement")
            waitFor { at ContributionAgreementPage }

        then:
            GebTestUtils.verifyContributionAgreement(browser, new Date(), "John Smith", "10 Some Street", "Some Village", "Some City", "Some Postcode", "Some Country", "£1,000.00", true, "My Company")
    }
}
