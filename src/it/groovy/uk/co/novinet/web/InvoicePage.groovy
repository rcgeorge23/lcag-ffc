package uk.co.novinet.web

import geb.Page

class InvoicePage extends Page {

    static url = "http://localhost:8484/invoice"

    static at = { title == "Loan Charge Action Group | Fighting Fund Contribution Invoice" }

    static content = {
        reference { $("#reference") }
        invoiceCreatedDate { $("#invoiceCreatedDate") }
        paymentReceivedDate { $("#paymentReceivedDate") }
        paymentMethod { $("#paymentMethod") }
        invoiceRecipientName { $("#invoiceRecipientName") }
        invoiceRecipientEmailAddress { $("#invoiceRecipientEmailAddress") }
        contributionType { $("#contributionType") }
        netAmount(required: false, wait: 2) { $("#netAmount") }
        vatPercentage(required: false, wait: 2) { $("#vatPercentage") }
        vatAmount(required: false, wait: 2) { $("#vatAmount") }
        vatNumber { $("#vatNumber") }
        grossAmount { $("#grossAmount") }
    }
}
