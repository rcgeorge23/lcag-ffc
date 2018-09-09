<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core' %>
<html>
    <%@page contentType="text/html" pageEncoding="UTF-8"%>
    <head>
        <link rel="stylesheet" href="/css/lcag-contribution-agreement.css" />
        <title>Loan Charge Action Group Fighting Fund Company | Contribution Agreement</title>
    </head>
    <body>
        <div class="contribution-agreement-box">
            <table cellpadding="0" cellspacing="0">
                <tr class="top">
                    <td colspan="2" align="center">
                        <strong>Contribution Agreement</strong>
                    </td>
                </tr>

                <tr class="information">
                    <td colspan="2">
                        Date: <span id="contributionAgreementDate">${payment.uiFriendlyPaymentReceivedDate}</span><br/><br/>

                        The following agreement is made between<br/><br/>

                        <strong>The Loan Charge Action Group Fighting Fund Company</strong> (LCAG FFC)<br/><br/>

                        Registered Address:<br/><br/>

                        11 Rothersthorpe<br/>
                        Giffard Park<br/>
                        Milton Keynes<br/>
                        MK14 5JL<br/><br/>

                        And<br/><br/>

                        <strong><span id="contributorName">${payment.firstName} ${payment.lastName}</span></strong> (the contributor)<br/><br/>

                        <span id="addressLine1">${payment.addressLine1}</span><br/>
                        <c:if test="${payment.addressLine2 != null && payment.addressLine2 != ''}">
                            <span id="addressLine2">${payment.addressLine2}</span><br/>
                        </c:if>
                        <span id="city">${payment.city}</span><br/>
                        <span id="postalCode">${payment.postalCode}</span><br/>
                        <span id="country">${payment.country}</span><br/><br/>

                        LCAG FFC hereby confirms that it has received the following ‘contribution’ from the contributor named above in the amount of <span id="grossAmount">${payment.uiFriendlyGrossAmount}</span> (including VAT).<br/><br/>

                        LCAG FFC agrees to return to the contributor such amounts as may be due under the terms and conditions agreed to at the point of application.<br/><br/>

                        Signed on behalf of LCAG FFC<br/><br/>

                        <img src="/images/richard_horsley_signature.png" style="max-width:150px;" width="150px;" /><br/><br/>
                        Richard Horsley CFO
                    </td>
                </tr>
            </table>
        </div>
    </body>
</html>