<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core' %>
<html>
    <%@page contentType="text/html" pageEncoding="UTF-8"%>
    <head>
        <link rel="stylesheet" href="/css/lcag-invoice.css" />
        <title>Loan Charge Action Group Fighting Fund Company | Invoice</title>
    </head>
    <body>
        <div class="invoice-box">
            <table cellpadding="0" cellspacing="0">
                <tr class="top">
                    <td colspan="2">
                        <table>
                            <tr>
                                <td class="title">
                                    <img src="/images/lcag_logo_small.png" style="max-width:150px;" width="150px;" />
                                </td>

                                <td>
                                    Reference: <span id="reference">${payment.reference}</span><br/>
                                    Invoice created: <span id="invoiceCreatedDate">${payment.uiFriendlyInvoiceCreatedDate}</span><br/>
                                    Payment received: <span id="paymentReceivedDate">${payment.uiFriendlyPaymentReceivedDate}</span><br/>
                                    Payment method: <span id="paymentMethod">${payment.paymentMethod}</span><br/>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>

                <tr class="information">
                    <td colspan="2">
                        <table>
                            <tr>
                                <td>

                                </td>

                                <td>
                                    <span id="invoiceRecipientName">${payment.firstName} ${payment.lastName}</span><br/>
                                    <span id="invoiceRecipientEmailAddress">${payment.emailAddress}</span>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>

                <tr class="heading">
                    <td>
                        Item
                    </td>

                    <td>
                        Price
                    </td>
                </tr>

                <tr class="last">
                    <td>
                        <span id="contributionType">Contribution Agreement</span>
                    </td>

                    <td>
                        <span>${payment.uiFriendlyGrossAmount}</span>
                    </td>
                </tr>

                <tr class="total">
                    <td></td>

                    <td>
                       Total: <span id="grossAmount">${payment.uiFriendlyGrossAmount}</span>
                    </td>
                </tr>

            </table>
        </div>
        <div class="footer">
            <small>LOAN CHARGE ACTION GROUP FFC | Company number 11528979</small><br/>
            <small>11 Rothersthorpe, Giffard Park, Milton Keynes, MK14 5JL, United Kingdom</small><br/>
        </div>
    </body>
</html>