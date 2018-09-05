package uk.co.novinet.web

import geb.spock.GebSpec
import uk.co.novinet.e2e.TestUtils
import uk.co.novinet.rest.PaymentType
import uk.co.novinet.service.ContributionType

import java.text.SimpleDateFormat

import static org.apache.commons.lang3.StringUtils.isBlank
import static uk.co.novinet.e2e.TestUtils.*
import static uk.co.novinet.web.GebTestUtils.*

class ClientSideValidationIT extends GebSpec {

    public static final String AUTHORIZED_CARD = "4242424242424242"
    public static final String DECLINED_CARD = "4000000000000002"

    def setup() {
        TestUtils.setupDatabaseSchema()

        deleteAllMessages("user1@something.com")
        deleteAllMessages("harry@generous.com")
        getEmails("user1@something.com", "Inbox").size() == 0
        getEmails("harry@generous.com", "Inbox").size() == 0
    }

    def "anonymous donor validation nothing entered on form"() {
        given:
        GebTestUtils.driveToPaymentType(browser, PaymentType.ANONYMOUS, ContributionType.DONATION, false)

        when: "i try to submit the form"
            payNowButton.click()

        then: "i am still on the form page and validation errors are displayed"
            waitFor { at LcagFfcFormPage }
            waitFor { grossAmountError == "This field is required." }
            waitFor { cardError == "Your card number is incomplete." }
    }

    def "anonymous donor validation less than £1"() {
        given:
        GebTestUtils.driveToPaymentType(browser, PaymentType.ANONYMOUS, ContributionType.DONATION, false)

        when: "i try to submit the form"
            amountInput = 0.99
            GebTestUtils.enterCardDetails(browser, "4242424242424242", "1222", "111", "22222")
            payNowButton.click()

        then: "i am still on the form page and validation errors are displayed"
            waitFor { at LcagFfcFormPage }
            waitFor { grossAmountError == "Please enter a value greater than or equal to 1." }
            waitFor { cardError.empty }
    }

    def "anonymous donor validation less than £1000"() {
        given:
            GebTestUtils.driveToPaymentType(browser, PaymentType.EXISTING_LCAG_MEMBER, ContributionType.CONTRIBUTION_AGREEMENT, false)

        when: "i try to submit the form"
            amountInput = 999.99
            GebTestUtils.enterCardDetails(browser, "4242424242424242", "1222", "111", "22222")
            payNowButton.click()

        then: "i am still on the form page and validation errors are displayed"
            waitFor { at LcagFfcFormPage }
            waitFor { grossAmountError == "Please enter a value greater than or equal to 1000." }
            waitFor { cardError.empty }
    }

    def "anonymous donor validation non numeric"() {
        given:
            GebTestUtils.driveToPaymentType(browser, PaymentType.ANONYMOUS, ContributionType.DONATION, false)

        when: "i try to submit the form"
            amountInput = "abc"
            GebTestUtils.enterCardDetails(browser, "4242424242424242", "1222", "111", "22222")
            payNowButton.click()

        then: "i am still on the form page and validation errors are displayed"
            waitFor { at LcagFfcFormPage }
            waitFor { grossAmountError == "Please specify an amount in GBP" }
            waitFor { cardError.empty }
    }

}

