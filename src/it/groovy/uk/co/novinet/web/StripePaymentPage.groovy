package uk.co.novinet.web

import geb.Page

class StripePaymentPage extends Page {

    static at = { title == "LCAG FFC" }

    static content = {
        creditCardInput { $("#cardNumber") }
        expiryDateInput { $("#cardExpiry") }
        c2vInput { $("#cardCvc") }
        billingNameInput { $("#billingName") }
        countryInput { $("#billingCountry") }
        postalCodeInput { $("#billingPostalCode") }
        submitButton { $("div.SubmitButton-Icon").last() }
        cardWasDeclinedErrorText { $("#cardNumber-fieldset div div").last().text() }
    }
}
