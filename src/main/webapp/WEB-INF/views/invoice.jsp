<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<html>
    <%@page contentType="text/html" pageEncoding="UTF-8"%>
    <head>
        <link rel="stylesheet" href="/css/lcag-invoice.css">
    </head>
    <body>
        <div class="invoice-box">
            <table cellpadding="0" cellspacing="0">
                <tr class="top">
                    <td colspan="2">
                        <table>
                            <tr>
                                <td class="title">
                                    <img src="/images/lcag_logo.jpg" style="width:40%; max-width:150px;">
                                </td>

                                <td>
                                    Reference: ${payment.reference}<br/>
                                    Invoice created: ${payment.uiFriendlyInvoiceCreatedDate}<br/>
                                    Payment received: ${payment.uiFriendlyPaymentReceivedDate}<br/>
                                    Payment method: ${payment.paymentMethod}<br/>
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
                                    LC Action Group LBG<br/>
                                    Sidwell<br/>
                                    Potmans Lane<br/>
                                    Bexhill-On-Sea<br/>
                                    East Sussex<br/>
                                    United Kingdom<br/>
                                </td>

                                <td>
                                    ${payment.firstName} ${payment.lastName}<br/>
                                    ${payment.emailAddress}
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

                <tr class="item">
                    <td>
                        ${payment.contributionType.friendlyName}
                    </td>

                    <td>
                        ${payment.uiFriendlyNetAmount}
                    </td>
                </tr>

                <tr class="item last">
                    <td>
                        VAT @ <fmt:formatNumber type="percent" maxFractionDigits="1" groupingUsed="false" value="${payment.vatRate / 100}" />
                    </td>

                    <td>
                        ${payment.uiFriendlyVarAmount}
                    </td>
                </tr>

                <tr class="total">
                    <td></td>

                    <td>
                       Total: ${payment.uiFriendlyGrossAmount}
                    </td>
                </tr>
            </table>
        </div>
    </body>
</html>