<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core' %>
<html>
    <%@page contentType="text/html" pageEncoding="UTF-8"%>
    <head>
        <link rel="stylesheet" href="/css/lcag-invoice.css" />
        <title>Loan Charge Action Group | Fighting Fund Contribution Invoice</title>
    </head>
    <body>
        <div class="invoice-box">
            <table cellpadding="0" cellspacing="0">
                <tr class="top">
                    <td colspan="2">
                        <table>
                            <tr>
                                <td class="title">
                                    <img src="/images/lcag_logo_small.jpg" style="max-width:150px;" width="150px;" />
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
                                    The Loan Charge Action Group Fighting Fund Company<br/>
                                    11 Rothersthorpe<br/>
                                    Giffard Park<br/>
                                    Milton Keynes<br/>
                                    MK14 5JL<br/>
                                    United Kingdom<br/>
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

                <tr class="item ${payment.vatNumber != '<PENDING>' ? '' : 'last'}">
                    <td>
                        <span id="contributionType">${payment.contributionType.friendlyName}</span>
                    </td>

                    <td>
                        <span id="netAmount">${payment.vatNumber == '<PENDING>' ? payment.uiFriendlyGrossAmount : payment.uiFriendlyNetAmount}</span>
                    </td>
                </tr>

                <c:if test="${payment.vatNumber != '<PENDING>'}">
                    <tr class="item last">
                        <td>
                            VAT @ <span id="vatPercentage"><fmt:formatNumber type="percent" maxFractionDigits="1" groupingUsed="false" value="${payment.vatRate / 100}" /></span>
                        </td>

                        <td>
                            <span id="vatAmount">${payment.uiFriendlyVatAmount}</span>
                        </td>
                    </tr>
                </c:if>

                <tr class="total">
                    <td></td>

                    <td>
                       Total: <span id="grossAmount">${payment.uiFriendlyGrossAmount}</span>
                    </td>
                </tr>

                <tr>
                    <td colspan="2">
                        <c:choose>
                            <c:when test="${payment.vatNumber == '<PENDING>'}">
                                <small><span id="vatNumber">A VAT invoice will be issued once LCAG FFC has finalised VAT registration.</span></small>
                            </c:when>
                            <c:otherwise>
                                <small><span id="vatNumber">VAT number: ${payment.vatNumber}</span></small>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </table>
        </div>
    </body>
</html>