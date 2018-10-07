package uk.co.novinet.web

import geb.Page

class SignContributionAgreementPage extends Page {

    static url = "http://localhost:8484/signContributionAgreement"

    static at = { title == "Loan Charge Action Group Fighting Fund Company | Contribution Agreement" }

    static content = {
        paymentReference { $("#paymentReference") }
        clearSignatureButton { $("#clearSignature") }
        saveSignatureButton { $("#saveSignature") }
        signatureCanvas(required: false, wait: 2) { $("canvas") }
    }
}
