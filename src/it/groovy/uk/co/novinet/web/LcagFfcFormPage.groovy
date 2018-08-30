package uk.co.novinet.web

import geb.Page

class LcagFfcFormPage extends Page {

    static url = "http://localhost:8484"

    static at = { title == "Loan Charge Action Group | Fighting Fund Contribution Form" }

    static content = {
        paymentDeclinedSection { $("#paymentDeclinedSection") }
        termsAndConditionsSection { $("#termsAndConditionsSection") }
        acceptTermsAndConditionsButton { $("#acceptTermsAndConditions") }
        paymentFormSection { $("#paymentFormSection") }
        newLcagJoinerInfoSection { $("#newLcagJoinerInfoSection") }

        existingLcagAccountInput { $("input[name=existingLcagAccount]") }
        existingLcagAccountYes { $("#existingLcagAccountYes") }
        existingLcagAccountNo { $("#existingLcagAccountNo") }
        existingLcagAccountAnonymous { $("#existingLcagAccountAnonymous") }

        contributionTypeInput { $("#contributionType") }
        contributionTypeDonation { $("#contributionTypeDonation") }
        contributionTypeContributionAgreement { $("#contributionTypeContributionAgreement") }

        donationInfoSection { $("#donationInfoSection") }
        contributionAgreementInfoSection { $("#contributionAgreementInfoSection") }

        usernameInput { $("#username") }
        firstNameInput { $("#firstName") }
        lastNameInput { $("#lastName") }
        emailAddressInput { $("#emailAddress") }

        amountInput { $("#grossAmount") }
        creditCardInput { $("input[name=cardnumber]") }
        expiryDateInput { $("input[name=exp-date]") }
        c2vInput { $("input[name=cvc]") }
        postalCodeInput { $("input[name=postal]") }

        payNowButton { $("#submitButton") }

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
