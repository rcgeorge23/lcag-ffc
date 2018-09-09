package uk.co.novinet.web

import geb.Page

class ContributionAgreementPage extends Page {

    static url = "http://localhost:8484/invoice"

    static at = { title == "Loan Charge Action Group Fighting Fund Company | Contribution Agreement" }

    static content = {
        contributionAgreementDate { $("#contributionAgreementDate").text() }
        contributorName { $("#contributorName").text() }
        addressLine1 { $("#addressLine1").text() }
        addressLine2 { $("#addressLine2").text() }
        city { $("#city").text() }
        postalCode { $("#postalCode").text() }
        country { $("#country").text() }
        grossAmount { $("#grossAmount").text() }
    }
}
