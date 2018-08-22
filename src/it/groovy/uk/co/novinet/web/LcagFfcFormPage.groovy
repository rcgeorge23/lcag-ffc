package uk.co.novinet.web

import geb.Page

class LcagFfcFormPage extends Page {

    static url = "http://localhost:8484"

    static at = { title == "Loan Charge Action Group | Fighting Fund Contribution Form" }

    static content = {
        existingLcagAccountInput { $("input[name=existingLcagAccount]") }
        existingLcagAccountYes { $("#existingLcagAccountYes") }
        existingLcagAccountNo { $("#existingLcagAccountNo") }
        existingLcagAccountAnonymous { $("#existingLcagAccountAnonymous") }
        lcagUsernameSection { $("#lcagUsernameSection") }

        //errors
        titleError { $("#title-error") }
        firstNameError { $("#firstName-error") }
        lastNameError { $("#lastName-error") }
        emailAddressError { $("#emailAddress-error") }
        addressLine1Error { $("#addressLine1-error") }
        addressLine2Error(wait: false, required: false) { $("#addressLine2-error") }
        cityError { $("#city-error") }
        postcodeError { $("#postcode-error") }
        phoneNumberError { $("#phoneNumber-error") }
        countryError { $("#country-error") }
        canShowWrittenEvidenceError { $("#canShowWrittenEvidence-error") }
        schemeDetailsError { $("#schemeDetails-error") }
        schemeAdvisorDetailsError { $("#schemeAdvisorDetails-error") }
        additionalInformationError(wait: false, required: false) { $("#additionalInformation-error") }

        submitButton { $("#submitButton") }
    }
}
