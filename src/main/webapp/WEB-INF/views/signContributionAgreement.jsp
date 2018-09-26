<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core' %>
<html>
    <%@page contentType="text/html" pageEncoding="UTF-8"%>
    <head>
        <script src="https://code.jquery.com/jquery-3.3.1.min.js" integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
        <script src="https://cdn.jsdelivr.net/npm/signature_pad@2.3.2/dist/signature_pad.min.js"></script>

        <link rel="stylesheet" href="/css/lcag-contribution-agreement.css" />
        <title>Loan Charge Action Group Fighting Fund Company | Contribution Agreement</title>
    </head>
    <body>
        <div class="contribution-agreement-box">
            <jsp:include page="contributionAgreementFragment.jsp"/>
            <br/>
            <div style="align: right;">
                <canvas id="myCanvas" height="150" width="300" style="border:1px solid #000000;">
                </canvas>
            </div>
            <span id="contributionAgreementDate">${payment.uiFriendlyPaymentReceivedDate}</span>
            <a href="">Clear</a>
        </div>
        <script>
            var canvas = document.querySelector("canvas");
            var signaturePad = new SignaturePad(canvas);
        </script>
    </body>
</html>