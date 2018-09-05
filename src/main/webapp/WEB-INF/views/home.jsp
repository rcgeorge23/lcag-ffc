<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri='http://java.sun.com/jsp/jstl/core' %>
<html>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <script src="https://code.jquery.com/jquery-3.3.1.min.js" integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.1/moment.min.js"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/dropzone/5.4.0/min/dropzone.min.css" rel="stylesheet" />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/dropzone/5.4.0/min/dropzone.min.js"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/css/toastr.min.css" rel="stylesheet" />
    <script src="https:////cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-validate/1.17.0/jquery.validate.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-validate/1.17.0/additional-methods.min.js"></script>
    <link rel="stylesheet" href="/css/lcag.css">
    <script src="/js/lcag-common.js"></script>
    <script src="https://js.stripe.com/v3/"></script>
    <script src="/js/lcag-stripe.js"></script>
    <script src="/js/lcag-validation.js"></script>
    <script>
        lcag.Common.init();
    </script>
    <title>Loan Charge Action Group | Fighting Fund Contribution Form</title>
</head>
    <body>
        <nav class="navbar navbar-default">
            <div class="container-fluid">
                <div class="navbar-header">
                    <a href="#" class="navbar-brand">
                        <img alt="Brand" src="/images/lcag_logo.jpg" width="60">
                    </a>
                </div>
            </div>
        </nav>

        <div class="container-fluid">
            <div class="row">
                <div class="col-md-12 col-sm-12">
                    <c:if test="${payment != null && payment.errorDescription != null && payment.errorDescription != ''}">
                        <div role="alert" class="alert alert-danger alert-dismissible" id="paymentDeclinedSection">
                            <div class="row">
                                <div class="col-md-1 col-sm-2">
                                    <i class="fa fa-exclamation-triangle fa-2x"></i>
                                </div>
                                <div class="col-md-10 col-sm-9">
                                    We were unable to take your payment. The error was: <strong><span id="paymentDeclinedErrorMessage">${payment.errorDescription}</span></strong>. Please try again or use alternate card details.<br/><br/>
                                    If the problem persists please contact litigation@hmrcloancharge.info.
                                </div>
                                <div class="col-md-1 col-sm-1 pull-right">
                                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                        <span aria-hidden="true">&times;</span>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </c:if>
                    <div role="alert" class="alert alert-danger alert-dismissible" id="validationErrorSection" style="display: none;">
                        <div class="row">
                            <div class="col-md-1 col-sm-2">
                                <i class="fa fa-exclamation-triangle fa-2x"></i>
                            </div>
                            <div class="col-md-10 col-sm-9">
                                We were unable to take your payment. The error was: <strong><span id="validationErrorMessage"></span></strong>.<br/><br/>
                                If the problem persists please contact litigation@hmrcloancharge.info.
                            </div>
                            <div class="col-md-1 col-sm-1 pull-right">
                                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                        </div>
                    </div>

                    <div id="termsAndConditionsSection">
                        <div class="panel panel-default">
                            <div class="panel-heading">LCAG FFC contributor terms and conditions<div class="pull-right"><i class="fa fa-lock" aria-hidden="true"></i></div></div>
                            <div class="panel-body">
                                <h4>Glossary of terms</h4>
                                <ul>
                                    <li>Contributor – any person who makes a financial contribution to the legal proceedings, whether that is a donation or a contribution with a Contribution Agreement</li>
                                    <li>Contribution Agreement – an agreement between LCAG FFC and the contributor that stipulates that in the case of a win that the contributor is entitled to a refund of their contribution on a pro-rata basis after all LCAG FFC’s costs and disbursements have been met. Contribution agreements will only be entered in to for sums of <span class="contributionAgreementMinimumAmountGbp">${contributionAgreementMinimumAmountGbp}</span> or more.</li>
                                    <li>Donation – A donation is a sum of money contributed to the fund where there is no agreement for it to be returned. Donations are sums less than <span class="contributionAgreementMinimumAmountGbp">${contributionAgreementMinimumAmountGbp}</span>. Those who donate more than ${minimumContributionAmountForEnhancedSupport} will be entitled to an enhanced level of information to help them manage the loan charge.</li>
                                    <li>LCAG FFC – Loan Charge Action Group Fighting Fund Company</li>
                                </ul>
                                <h4>Terms and conditions</h4>
                                <ol>
                                    <li>Contributions can be made in the form of a Contribution Agreement or by a Donation.</li>
                                    <li>Contribution Agreements will be drawn up to reflect the amount of money contributed, the details of the contributor and the arrangements for any refunds of payments which may be due.</li>
                                    <li>Refunds will be made to Contributors making a Contribution Agreement on a pro rata basis and after administration costs have been deducted.</li>
                                    <li>Refunds to contributors making a Contribution Agreement will be subject to repayment only of the funds which are available following any payments received from the other party in the proceedings.  These are likely to be in the region of between 60% and 70% of the amount contributed originally in the event of a success.</li>
                                    <li>Full details of the Contribution Agreement will be sent to the contributor by email shortly after the contribution has been made.</li>
                                    <li>Donations are made on the basis that refunds will only be provided (less card processing fees) in the event that the litigation does not take place due to insufficient funds being raised.</li>
                                    <li>Contributors who make a Donation are asked to provide contact details so that they can be given access to the LCAG FCC Forum where progress reports and pertinent information regarding activities which may assist those contributors will be posted.</li>
                                    <li>Any person making a Donation does not have to provide contact details and can remain fully anonymous if they wish, however, they will not be privy to the additional information which is available on the LCAG FCC Forum in this event.</li>
                                    <li>It is anticipated that larger or corporate/organisation contributors will provide funding via a Contribution Agreement, however, Contribution Agreements are not limited to these contributors.</li>
                                    <li>It is anticipated that smaller donations or funding provided by individuals will be made by way of a Donation, however, Donations are not limited to these contributors.</li>
                                </ol>

                                <br />

                                <div class="form-group col-md-12">
                                    <button id="acceptTermsAndConditions" type="button" class="btn btn-primary btn-block" onclick="lcag.Validation.acceptTermsAndConditions();">I have read and agree to the terms and conditions</button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div id="paymentFormSection" style="display:none;">
                        <div class="alert alert-info">
                            <div class="row">
                                <div class="col-md-1 col-sm-2">
                                    <i class="fa fa-info-circle fa-2x"></i>
                                </div>
                                <div class="col-md-11 col-sm-10">
                                    <strong>GDPR information</strong>: The details that you provide will not be shared with third parties.</p>
                                </div>
                            </div>
                        </div>
                        <form action="/submit" method="post" id="payment-form">
                            <input type="hidden" id="paymentType" name="paymentType" value="" />
                            <input type="hidden" id="contributionType" name="contributionType" value="" />
                            <div class="panel panel-default">
                                <div class="panel-heading">Payment<div class="pull-right"><i class="fa fa-lock" aria-hidden="true"></i></div></div>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label>Do you already have an LCAG account?</label>
                                        <div>
                                            <label class="radio-inline"> <input class="update-fields" type="radio" name="existingLcagAccount" id="existingLcagAccountYes" value="yes" required> Yes </label>
                                            <label class="radio-inline"> <input class="update-fields" type="radio" name="existingLcagAccount" id="existingLcagAccountNo" value="no" required> No - I would like to join </label>
                                            <label class="radio-inline"> <input class="update-fields" type="radio" name="existingLcagAccount" id="existingLcagAccountAnonymous" value="anonymous" required> No - I would like to donate anonymously </label>
                                        </div>
                                    </div>
                                    <div id="newLcagJoinerInfoSection" style="display: none;">
                                        <div class="alert alert-info">
                                            <div class="row">
                                                <div class="col-md-1 col-sm-2">
                                                    <i class="fa fa-info-circle fa-2x"></i>
                                                </div>
                                                <div class="col-md-11 col-sm-10">
                                                    A new LCAG forum account will be created and joining instructions emailed to you once you complete this form and submit your payment.
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div id="contributionTypeSection" style="display: none;">
                                        <div class="form-group">
                                            <label>My contribution will be a:</label>
                                            <div>
                                                <label class="radio-inline"> <input class="update-fields" type="radio" name="contributionTypeRadio" id="contributionTypeDonation" value="DONATION" required> Donation </label>
                                                <label class="radio-inline"> <input class="update-fields" type="radio" name="contributionTypeRadio" id="contributionTypeContributionAgreement" value="CONTRIBUTION_AGREEMENT" required> Contribution Agreement </label>
                                            </div>
                                        </div>
                                        <div class="alert alert-info" id="contributionAgreementInfoSection" style="display: none;">
                                            <div class="row">
                                                <div class="col-md-1 col-sm-2">
                                                    <i class="fa fa-info-circle fa-2x"></i>
                                                </div>
                                                <div class="col-md-11 col-sm-10">
                                                    Payments made as <strong>Contribution Agreements</strong> will be partially refunded in the event of a successful litigation outcome as outlined in the terms and conditions above OR if insufficient funds are raised and the litigation does not proceed (less transaction fees).<br/><br/>
                                                    Minimum payment for a Contribution Agreement is <span class="contributionAgreementMinimumAmountGbp">${contributionAgreementMinimumAmountGbp}</span>.
                                                </div>
                                            </div>
                                        </div>
                                        <div class="alert alert-info" id="donationInfoSection" style="display: none;">
                                            <div class="row">
                                                <div class="col-md-1 col-sm-2">
                                                    <i class="fa fa-info-circle fa-2x"></i>
                                                </div>
                                                <div class="col-md-11 col-sm-10">
                                                    Payments made as <strong>Donations</strong> will only be refunded to the card holder (less transaction fees) in the event that insufficient funds are raised and the litigation does not proceed.<br/><br/>
                                                    The minimum payment for donations is £1.
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div id="vatSection" style="display: none;">
                                        <div class="form-group">
                                            <label>I am making this contribution on behalf of a VAT registered company:</label>
                                            <a class='form-tooltip' data-toggle="tooltip" data-placement="right" title="You must only answer yes to this question if you are making a contribution on behalf of a VAT registered company.">
                                                <i class='glyphicon glyphicon-info-sign'></i>
                                            </a>
                                            <div>
                                                <label class="radio-inline"> <input class="update-fields" type="radio" name="contributorIsVatRegistered" id="contributorIsVatRegisteredYes" value="yes" required> Yes </label>
                                                <label class="radio-inline"> <input class="update-fields" type="radio" name="contributorIsVatRegistered" id="contributorIsVatRegisteredNo" value="no" required> No </label>
                                            </div>
                                        </div>
                                    </div>
                                    <div id="lcagUsernameSection" style="display: none;">
                                        <div class="form-group">
                                            <label for="username">LCAG username:</label>
                                            <div class="input-group">
                                                <div class="input-group-addon"><i class="fa fa-user" aria-hidden="true"></i></div>
                                                <input type="text" name="username" class="form-control" id="username" placeholder="Please enter your LCAG username" required />
                                            </div>
                                        </div>
                                    </div>
                                    <div id="companyNameSection" style="display: none;">
                                        <div class="form-group">
                                            <label for="companyName">Company name:</label>
                                            <div class="input-group">
                                                <div class="input-group-addon"><i class="fa fa-align-justify" aria-hidden="true"></i></div>
                                                <input type="text" name="companyName" class="form-control" id="companyName" placeholder="Please enter your company name" required />
                                            </div>
                                        </div>
                                    </div>
                                    <div id="nameSection" style="display: none;">
                                        <div class="form-group">
                                            <label for="firstName">First name:</label>
                                            <div class="input-group">
                                                <div class="input-group-addon"><i class="fa fa-align-justify" aria-hidden="true"></i></div>
                                                <input type="text" name="firstName" class="form-control" id="firstName" placeholder="Please enter your first name" required />
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label for="lastName">Last name:</label>
                                            <div class="input-group">
                                                <div class="input-group-addon"><i class="fa fa-align-justify" aria-hidden="true"></i></div>
                                                <input type="text" name="lastName" class="form-control" id="lastName" placeholder="Please enter your last name" required />
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label for="emailAddress">Email address:</label>
                                            <div class="input-group">
                                                <div class="input-group-addon">@</div>
                                                <input type="email" name="emailAddress" class="form-control" id="emailAddress" placeholder="Please enter your email address" required />
                                            </div>
                                        </div>
                                    </div>
                                    <div id="addressSection" style="display: none;">
                                        <div class="form-group">
                                            <label for="addressLine1">Address line 1:</label>
                                            <div class="input-group">
                                                <div class="input-group-addon"><i class="fa fa-align-justify" aria-hidden="true"></i></div>
                                                <input type="text" name="addressLine1" class="form-control" id="addressLine1" placeholder="Please enter the first line of your address" required />
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label for="addressLine1">Address line 2:</label>
                                            <div class="input-group">
                                                <div class="input-group-addon"><i class="fa fa-align-justify" aria-hidden="true"></i></div>
                                                <input type="text" name="addressLine2" class="form-control" id="addressLine2" placeholder="Please enter the second line of your address" />
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label for="city">City:</label>
                                            <div class="input-group">
                                                <div class="input-group-addon"><i class="fa fa-align-justify" aria-hidden="true"></i></div>
                                                <input type="text" name="city" class="form-control" id="city" placeholder="Please enter your city" required />
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label for="city">Postal code:</label>
                                            <div class="input-group">
                                                <div class="input-group-addon"><i class="fa fa-align-justify" aria-hidden="true"></i></div>
                                                <input type="text" name="postalCode" class="form-control" id="postalCode" placeholder="Please enter your postal code" required />
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label for="city">Country:</label>
                                            <div class="input-group">
                                                <div class="input-group-addon"><i class="fa fa-align-justify" aria-hidden="true"></i></div>
                                                <input type="text" name="country" class="form-control" id="country" placeholder="Please enter your country" required />
                                            </div>
                                        </div>
                                    </div>
                                    <div id="paymentFieldsSection" class="form-group" style="display: none;">
                                        <label for="grossAmount">Amount:</label>
                                        <div class="form-group">
                                            <div class="input-group">
                                                <div class="input-group-addon">£</div>
                                                <input type="text" name="grossAmount" class="form-control" id="grossAmount" placeholder="Please enter the amount you wish to contribute" required />
                                            </div>
                                        </div>

                                        <label for="card-element">Credit or debit card:</label>
                                        <div id="card-element" classes="form-control">
                                        </div>
                                        <div id="card-errors" class="error help-block" role="alert"></div>
                                    </div>
                                </div>
                                <div class="panel-footer clearfix">
                                    <button type="submit" class="btn btn-primary btn-block" id="submitButton" style="display: none;">Pay Now</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <script type="text/javascript">
            lcag.Stripe.init();
            lcag.Validation.init();

            $(function () {
                $("a.form-tooltip").tooltip();

                $("input[name=existingLcagAccount]").change(function() {
                    lcag.Validation.displayFieldsAndSetupValidationRules();
                });

                $("input[name=contributionTypeRadio]").change(function() {
                    lcag.Validation.displayFieldsAndSetupValidationRules();
                });

                $("input[name=contributorIsVatRegistered]").change(function() {
                    lcag.Validation.displayFieldsAndSetupValidationRules();
                });
            });


        </script>

    </body>
</html>
