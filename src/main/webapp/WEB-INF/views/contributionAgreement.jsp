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
            <jsp:include page="contributionAgreementFragment.jsp"/>
            <br/>
            <div style="width: 100%;">
                <img style="" src="${payment.signatureData}" />
                <br/><br/>
            </div>
        </div>
    </body>
</html>