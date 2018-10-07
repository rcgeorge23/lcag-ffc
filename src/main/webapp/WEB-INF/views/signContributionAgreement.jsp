<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core' %>
<html>
    <%@page contentType="text/html" pageEncoding="UTF-8"%>
    <head>
        <script src="https://code.jquery.com/jquery-3.3.1.min.js" integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
        <script src="https://cdn.jsdelivr.net/npm/signature_pad@2.3.2/dist/signature_pad.min.js"></script>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/css/toastr.min.css" rel="stylesheet" />
        <script src="https:////cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.js"></script>
        <script src="/js/lcag-common.js"></script>
        <link rel="stylesheet" href="/css/lcag-sign-contribution-agreement.css" />
        <title>Loan Charge Action Group Fighting Fund Company | Contribution Agreement</title>
    </head>
    <body>
    <body>
        <nav class="navbar navbar-default">
            <div class="container-fluid">
                <div class="navbar-header">
                    <a href="#" class="navbar-brand">
                        <img alt="Brand" src="/images/lcag_logo.png" width="60">
                    </a>
                </div>
            </div>
        </nav>

        <div class="container">
            <div class="row">
                <div class="col-md-12">
                    <div class="panel panel-default">

                        <div class="panel-heading">Thank you for your payment - please sign the Contribution Agreement<div class="pull-right"><i class="fa fa-lock" aria-hidden="true"></i></div></div>

                        <div class="panel-body">
                            <p>Thank you for your litigation contribution. Your payment reference is <span id="paymentReference">${payment == null ? '...' : payment.reference}</span>.</p>
                            <p>A <strong>Contribution Agreement</strong> document has been drawn up to reflect the amount of money that you have contributed.</p>
                            <p>In order to complete the process you must electronically sign the Contribution Agreement document below.</p>
                            <hr/>
                            <div class="contribution-agreement-box">
                                <jsp:include page="contributionAgreementFragment.jsp"/>
                                <br/>
                                Your signature:
                                <div style="align: right;">
                                    <canvas height="150" width="450" style="border:1px solid #000000;">
                                    </canvas>
                                </div>
                            </div>
                            <form action="/signContributionAgreement" method="post" id="signature-form">
                                <input type="hidden" name="signatureData" value="" />
                                <input type="hidden" name="guid" value="${guid}" />
                            </form>
                        </div>
                        <div class="panel-footer clearfix text-right">
                            <button id="clearSignature" type="button" class="btn btn-primary" id="submitButton">Clear Signature</button>
                            <button id="saveSignature" type="button" class="btn btn-primary" id="submitButton">Submit</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script>
            var canvas = document.querySelector("canvas");
            var signaturePad = new SignaturePad(canvas);

            document.getElementById('clearSignature').addEventListener('click', function(e) {
                e.preventDefault();
                signaturePad.clear();
            });

            document.getElementById('saveSignature').addEventListener('click', function(e) {
                e.preventDefault();

                if (signaturePad.isEmpty()) {
                    lcag.Common.alertError("Please provide a signature.");
                    return;
                }

                $("input[name=signatureData]").val(signaturePad.toDataURL('image/png'));

                $("#signature-form").submit();
            });
        </script>
    </body>


</html>