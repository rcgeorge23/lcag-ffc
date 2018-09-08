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
                                    <li>Contribution Agreement – an agreement between LCAG FFC and the contributor that stipulates that in the case of a win that the contributor is entitled to a refund of their contribution on a pro-rata basis after all LCAG FFC’s costs and disbursements have been met. Contribution Agreements will only be entered in to for sums of <span class="contributionAgreementMinimumAmountGbp">${formattedContributionAgreementMinimumAmountGbp}</span> or more.</li>
                                    <li>Donation – A sum of money contributed to the fund where there is no agreement for it to be returned. Donations are sums less than <span class="contributionAgreementMinimumAmountGbp">${formattedContributionAgreementMinimumAmountGbp}</span>.</li>
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
                                    <li>Contributors are asked to provide contact details so that they can be given access to the LCAG FCC forum where progress reports and pertinent information regarding activities which may assist those contributors will be posted.</li>
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
                                    <strong>GDPR information</strong>: The details that you provide will not be shared with third parties however it is possible that they may be seen by the small team of LCAG volunteers responsible for membership administration.<br/><br/>
                                    LCAG will not store your credit card details.</p>
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
                                        <label for="grossAmount">Contribution amount:</label>
                                        <div class="form-group">
                                            <div class="input-group">
                                                <div class="input-group-addon">£</div>
                                                <input type="text" name="grossAmount" class="form-control" id="grossAmount" placeholder="Please enter the amount you wish to contribute" required />
                                            </div>
                                        </div>
                                    </div>
                                    <div class="alert alert-info" id="contributionAgreementInfoSection" style="display: none;">
                                        <div class="row">
                                            <div class="col-md-1 col-sm-2">
                                                <i class="fa fa-info-circle fa-2x"></i>
                                            </div>
                                            <div class="col-md-11 col-sm-10">
                                                You are making a contribution of £<span class="gross-amount"></span><br/><br/>
                                                Contributions of <span class="contributionAgreementMinimumAmountGbp">${formattedContributionAgreementMinimumAmountGbp}</span> or more can be made by <strong>Contribution Agreement</strong>.<br/><br/>
                                                Payments made as Contribution Agreements will be partially refunded in the event of a successful litigation outcome as outlined in the terms and conditions above OR if insufficient funds are raised and the litigation does not proceed to the first stage (less transaction fees).<br/><br/>
                                                You may still choose to pay by Donation if you wish to remain anonymous or are not concerned with receiving a partial repayment in the event of a successful litigation outcome.<br/><br/>
                                                <div id="contributionTypeSection">
                                                    <div class="form-group">
                                                        <label>My contribution will be a:</label>
                                                        <div>
                                                            <label class="radio-inline"> <input class="update-fields" type="radio" name="contributionTypeRadio" id="contributionTypeDonation" value="DONATION" required> Donation </label>
                                                            <label class="radio-inline"> <input class="update-fields" type="radio" name="contributionTypeRadio" id="contributionTypeContributionAgreement" value="CONTRIBUTION_AGREEMENT" required> Contribution Agreement </label>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="alert alert-info" id="donationInfoSection" style="display: none;">
                                        <div class="row">
                                            <div class="col-md-1 col-sm-2">
                                                <i class="fa fa-info-circle fa-2x"></i>
                                            </div>
                                            <div class="col-md-11 col-sm-10">
                                                You are making a contribution of £<span class="gross-amount"></span><br/><br/>
                                                Contributions between £1 and <span class="contributionAgreementMinimumAmountGbp">${formattedContributionAgreementMinimumAmountGbp}</span> are classed as <strong>Donations</strong>.<br/><br/>
                                                Donations will only be refunded to the card holder (less transaction fees) in the event that insufficient funds are raised and the litigation does not proceed to the first stage.<br/><br/>
                                            </div>
                                        </div>
                                    </div>
                                    <div id="existingLcagAccountSection" style="display:none;">
                                        <div class="form-group">
                                            <label>Do you already have an LCAG account?</label>
                                            <div>
                                                <label class="radio-inline"> <input class="update-fields" type="radio" name="existingLcagAccount" id="existingLcagAccountYes" value="yes" required> Yes </label>
                                                <label class="radio-inline"> <input class="update-fields" type="radio" name="existingLcagAccount" id="existingLcagAccountNo" value="no" required> No - I would like to join </label>
                                                <label class="radio-inline"> <input class="update-fields" type="radio" name="existingLcagAccount" id="existingLcagAccountAnonymous" value="anonymous" required> No - I would like to donate anonymously </label>
                                            </div>
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
                                    <div id="lcagUsernameSection" style="display: none;">
                                        <div class="form-group">
                                            <label for="username">LCAG username:</label>
                                            <div class="input-group">
                                                <div class="input-group-addon"><i class="fa fa-user" aria-hidden="true"></i></div>
                                                <input type="text" name="username" class="form-control" id="username" placeholder="Please enter your LCAG username" required />
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

<!-- Button trigger modal -->
<div class="text-center"><a href="#" data-toggle="modal" data-target="#privacyPolicyModal">Privacy Policy</a></div>

<!-- Modal -->
<div class="modal fade" id="privacyPolicyModal" tabindex="-1" role="dialog" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title" id="privacyPolicyModalLabel">Privacy Policy</h4>
      </div>
      <div class="modal-body">

        <p>Your data is very important, so we are committed to maintaining the trust and confidence of everyone who contacts us. We are GDPR-compliant but strive to go above and beyond this standard.</p>

        <p>Whether you contact us, join our forum, or donate money to the litigation fund, we will do our best to protect your privacy and keep your data safe and secure.</p>

        <p>LCAG does use cookies, encryption and individual login IDs</p>

        <p>On request we will remove all information concerning you / authenticated check prior to deletion</p>

        <p>LCAG does not:</p>
        <ul>
            <li>collect any other data apart from that used to identify the user</li>
            <li>profile, analyse, or record further information</li>
            <li>share with external 3rd parties any data / or transfers data outside the UK / EU</li>
            <li>collect sensitive data</li>
            <li>record telephone or web traffic</li>
        </ul>

        <h4>Cookies Policy</h4>

        <p>We use a system of classifying the different types of cookies which we use on the Website, or which may be used by third parties through our websites. The classification was developed by the International Chamber of Commerce UK and explains more about which cookies we use, why we use them, and the functionality you will lose if you decide you don’t want to have them on your device.</p>

        <h5>Strictly necessary cookies</h5>
        <p>These cookies enable services you have specifically asked for. These cookies are essential in order to enable you to move around the website and use its features, such as accessing secure areas of the website.</p>

        <h5>Performance cookies</h5>
        <p>These cookies collect anonymous information on the pages visited. By using the website, you agree that we can place these types of cookies on your device.</p>

        <p>These cookies collect information about how visitors use the website, for instance which pages visitors go to most often, and if they get error messages from web pages. These cookies don’t collect information that identifies a visitor. All information these cookies collect is aggregated and therefore anonymous. It is only used to improve how the Website works.</p>

        <h5>Functionality cookies</h5>
        <p>These cookies remember choices you make to improve your experience.  By using the Website, you agree that we can place these types of cookies on your device.</p>

        <p>These cookies allow the Website to remember choices you make (such as your user name, language or the region you are in) and provide enhanced, more personal features. These cookies can also be used to remember changes you have made to text size, fonts and other parts of web pages that you can customise. They may also be used to provide services you have asked for such as watching a video or commenting on a blog. The information these cookies collect may be anonymised and they cannot track your browsing activity on other websites.</p>

        <h5>Third party cookies</h5>
        <p>These cookies allow third parties to track the success of their application or customise the application for you. Because of how cookies work we cannot access these cookies, nor can the third parties access the data in cookies used on our site.</p>

        <p>For example, if you choose to ‘share’ content through Twitter or other social networks you might be sent cookies from these websites. We don’t control the setting of these cookies, so please check those websites for more information about their cookies and how to manage them.</p>

        <h4>Managing Cookies</h4>
        <p>If you do not wish to accept cookies on to your device you can do so by adjusting the settings on your browser. However, be aware that if you do block cookies, some features of our websites may not be available to you and some web pages may not display properly.</p>

        <p><a href="http://aboutcookies.org/">AboutCookies.org</a> provides a guide to how you can do this on the most commonly used browsers, or you can visit the following webpages:</p>

        <p><a href="https://support.google.com/chrome/answer/95647">Google Chrome</a></p>
        <p><a href="https://support.microsoft.com/en-us/help/17442/windows-internet-explorer-delete-manage-cookies">Microsoft Internet Explorer</a></p>
        <p><a href="https://support.mozilla.org/en-US/kb/enable-and-disable-cookies-website-preferences">Mozilla Firefox</a></p>

        <p>If you have concerns about any cookies set by the British Museum please contact info@britishmuseum.org, and include a link to the page on which the cookie is set.</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary">Save changes</button>
      </div>
    </div>
  </div>
</div>




        <script type="text/javascript">
            lcag.Stripe.init("${publishableStripeApiKey}");
            lcag.Validation.init("${contributionAgreementMinimumAmountGbp}");

            $(function () {
                $("a.form-tooltip").tooltip();

                $("input[name=grossAmount]").keyup(function() {
                    lcag.Validation.updateOtherGrossAmountValues();
                    lcag.Validation.displayFieldsAndSetupValidationRules();
                });

                $("input[name=grossAmount]").change(function() {
                    lcag.Validation.displayFieldsAndSetupValidationRules();
                });

                $("input[name=existingLcagAccount]").change(function() {
                    lcag.Validation.displayFieldsAndSetupValidationRules();
                });

                $("input[name=contributionTypeRadio]").change(function() {
                    lcag.Validation.displayFieldsAndSetupValidationRules();
                });
            });


        </script>

    </body>
</html>
