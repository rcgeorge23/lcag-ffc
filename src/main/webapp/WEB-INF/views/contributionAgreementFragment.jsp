<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core' %>

<table cellpadding="0" cellspacing="0">
    <tr class="top">
        <td colspan="2" align="center">
            <h2>LOAN CHARGE ACTION GROUP FFC</h2>
            <strong>CONTRIBUTION AGREEMENT</strong>
        </td>
    </tr>

    <tr><td><br/><br/></td></tr>

    <tr class="information">
        <td colspan="2">
            To the Directors of<br/>
            <strong>The Loan Charge Action Group Fighting Fund Company</strong> (LCAG FFC)<br/>
            11 Rothersthorpe<br/>
            Giffard Park<br/>
            Milton Keynes<br/>
            MK14 5JL<br/><br/>

            I <strong><span id="contributorName">${payment.firstName} ${payment.lastName}</span></strong> of<br/>
            <span id="addressLine1">${payment.addressLine1}</span><br/>
            <c:if test="${payment.addressLine2 != null && payment.addressLine2 != ''}">
                <span id="addressLine2">${payment.addressLine2}</span><br/>
            </c:if>
            <span id="city">${payment.city}</span><br/>
            <span id="postalCode">${payment.postalCode}</span><br/>
            <span id="country">${payment.country}</span><br/><br/>

            declare that I have made a payment via credit / debit card to LOAN CHARGE ACTION GROUP FFC on
            <span id="contributionAgreementDate">${payment.uiFriendlyPaymentReceivedDate}</span> the amount of
            <span id="grossAmount">${payment.uiFriendlyGrossAmount}</span> which amount is a contribution to be held by
            LOAN CHARGE ACTION GROUP FFC as an addition to the Trust Fund of a trust known as
            "THE LOAN CHARGE LITIGATION TRUST" of which LOAN CHARGE ACTION GROUP FFC is the Trustee.
        </td>
    </tr>
</table>