<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
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
                <div class="col-md-10 col-sm-12">
                    <div role="alert" class="alert alert-danger alert-dismissible" id="paymentDeclinedSection" style="display: none;">
                        <div class="row">
                            <div class="col-md-1 col-sm-2">
                                <i class="fa fa-exclamation-triangle fa-2x"></i>
                            </div>
                            <div class="col-md-10 col-sm-9">
                                We were unable to take your payment. The error was: <strong><span id="paymentDeclinedErrorMessage"></span></strong>. Please try again or use alternate card details.<br/><br/>
                                If the problem persists please contact litigation@hmrcloancharge.info.
                            </div>
                            <div class="col-md-1 col-sm-1 pull-right">
                                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                        </div>
                    </div>
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
                            <div class="panel-heading">LCAG fighting fund contributor terms and conditions<div class="pull-right"><i class="fa fa-lock" aria-hidden="true"></i></div></div>
                            <div class="panel-body">
                                <h4>Glossary of terms</h4>
                                <ul>
                                    <li>Contributor – any person who makes a financial contribution to the legal proceedings whether that is a donation or a contribution with a Contribution Agreement</li>
                                    <li>Contribution Agreement –</li>
                                    <li>Donation –</li>
                                    <li>LCAG FFC –</li>
                                </ul>
                                <h4>Terms and conditions</h4>
                                <ol>
                                    <li>Contributions can be made in the form of a Contribution Agreement or by a Donation.</li>
                                    <li>Contribution Agreements will be drawn up to reflect the amount of money contributed,
                                        the details of the contributor and the arrangements for any refunds of payments which
                                        may be due.</li>
                                    <li>Refunds will be made to Contributors making an Contribution Agreement on a pro rata
                                        basis and after administration costs have been deducted.</li>
                                    <li>Refunds to contributors making a Contribution Agreement will be subject to repayment
                                        only of the funds which are available following any payments received from the other
                                        party in the proceedings. These are likely to be in the region of between 60% and 70%
                                        of the amount contributed originally in the event of a success.</li>
                                    <li>Full details of the Contribution Agreement will be sent to the contributor as soon as
                                        possible after the contribution is made, and in any even within 14 days.</li>
                                    <li>As a contributor, advice will be provided in relation to declaration of loans,
                                        resisting attempts to enforce the Loan Charge by HMRC and any relevant appeals that may
                                        be necessary pending the resolution of the litigation.</li>
                                    <li>Contributors who do not return the Contribution Agreement or confirmation thereof, or
                                        can not be reached using the contact details provided will be treated as having
                                        made a donation after a period of 28 days of the initial Contribution Agreement being
                                        issued.</li>
                                    <li>Donations are made on the basis that no refunds will be given in any circumstances, this
                                        includes, but is not limited to; the case being successful and there being funds remaining
                                        in the account, the case being unsuccessful and there being funds remaining in the
                                        account, insufficient funds being collected and the case not being able to proceed or
                                        being withdrawn at any stage in the proceedings.</li>
                                    <li>Contributors who make a Donation are asked to provide contact details so that they can
                                        be given access to the LCAG FCC Forum where progress reports and pertinent
                                        information regarding activities which may assist those contributors will be posted.</li>
                                    <li>Any person making a donation does not have to provide contact details and can remain
                                        fully anonymous if they wish, however, they will not be privy to the additional
                                        information which is available on the LCAG FCC Forum in this event.</li>
                                    <li>It is anticipated that larger or corporate/organisation contributors will provide
                                        funding via a Contribution Agreement, however, Contribution Agreements are not
                                        limited to these contributors.</li>
                                    <li>It is anticipated that smaller donations or funding provided by individuals will be
                                        made by way of a Donation, however, Donations are not limited to these contributors.</li>
                                </ol>

                                <br />

                                <div class="form-group col-md-12">
                                    <button id="acceptTermsAndConditions" type="button" class="btn btn-primary btn-block">I have read and agree to the terms and conditions</button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div id="paymentFormSection" style="display:none;">
                        <form action="/submit" method="post" id="payment-form">
                            <input type="hidden" id="paymentType" name="paymentType" value="" />
                            <input type="hidden" id="contributionType" name="contributionType" value="" />
                            <div class="panel panel-default">
                                <div class="panel-heading">Payment<div class="pull-right"><i class="fa fa-lock" aria-hidden="true"></i></div></div>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label>Do you already have an LCAG account?</label>
                                        <div>
                                            <label class="radio-inline"> <input type="radio" name="existingLcagAccount" id="existingLcagAccountYes" value="yes" required> Yes </label>
                                            <label class="radio-inline"> <input type="radio" name="existingLcagAccount" id="existingLcagAccountNo" value="no" required> No - I would like to join </label>
                                            <label class="radio-inline"> <input type="radio" name="existingLcagAccount" id="existingLcagAccountAnonymous" value="anonymous" required> No - I would like to donate anonymously </label>
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
                                                <label class="radio-inline"> <input type="radio" name="contributionTypeRadio" id="contributionTypeDonation" value="DONATION" required> Donation </label>
                                                <label class="radio-inline"> <input type="radio" name="contributionTypeRadio" id="contributionTypeContributionAgreement" value="CONTRIBUTION_AGREEMENT" required> Contribution Agreement </label>
                                            </div>
                                        </div>
                                        <div class="alert alert-info" id="contributionAgreementInfoSection" style="display: none;">
                                            <div class="row">
                                                <div class="col-md-1 col-sm-2">
                                                    <i class="fa fa-info-circle fa-2x"></i>
                                                </div>
                                                <div class="col-md-11 col-sm-10">
                                                    Payments made as <strong>Contribution Agreements</strong> will be partially refunded in the event of a successful litigation outcome as outlined in the terms and conditions above.<br/><br/>
                                                    Minimum payment for a Contribution Agreement is £<span id="contributionAgreementMinimumAmountGbp"></span>.
                                                </div>
                                            </div>
                                        </div>
                                        <div class="alert alert-info" id="donationInfoSection" style="display: none;">
                                            <div class="row">
                                                <div class="col-md-1 col-sm-2">
                                                    <i class="fa fa-info-circle fa-2x"></i>
                                                </div>
                                                <div class="col-md-11 col-sm-10">
                                                    Payments made as <strong>Donations</strong> will not be refunded under any circumstances.<br/><br/>
                                                    There is no minimum payment for donations.
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div id="newLcagJoinerSection" style="display: none;">
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
                                    <div id="paymentFieldsSection" class="form-group" style="display: none;">
                                        <label for="grossAmount">Amount:</label>
                                        <div class="form-group">
                                            <div class="input-group">
                                                <div class="input-group-addon">£</div>
                                                <input type="text" name="grossAmount" class="form-control" id="grossAmount" placeholder="Please enter the amount you wish to donate" required />
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

        <script src="https://js.stripe.com/v3/"></script>
        <script src="/js/lcag-stripe.js"></script>

        <script type="text/javascript">
            jQuery.validator.addMethod('currency',
                function(value, element) {
                    var result = value.match(/^\d{1,3}?([,]\d{3}|\d)*?([.]\d{1,2})?$/);
                    return result;
                },
                'Please specify an amount in GBP'
            );

            jQuery.validator.addMethod('lcagUsername',
                function(value, element) {
                    console.log("value", value);
                    console.log("element", element);

                    var validationResult = false;

                    jQuery.ajax({
                        url: "/member?username=" + value,
                        success: function (result) {
                            console.log("result", result);
                            validationResult = !(result == null || result == "");
                        },
                        async: false
                    });

                    return validationResult;
                },
                'This is not a valid LCAG username'
            );

            var payment = null;

            function getParameterByName(name, url) {
                if (!url) url = window.location.href;
                name = name.replace(/[\[\]]/g, "\\$&");
                var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
                    results = regex.exec(url);
                if (!results) return null;
                if (!results[2]) return '';
                return decodeURIComponent(results[2].replace(/\+/g, " "));
            }

            function showLcagUsernameSection() {
                $("#lcagUsernameSection").show();
                $("#newLcagJoinerSection").hide();
                $("#newLcagJoinerInfoSection").hide();
                $("#paymentFieldsSection").show();
                $("#username").prop('required',true);
                $("#emailAddress").removeAttr("required");
                $("#contributionTypeSection").show();
                $("#submitButton").show();
                $("#paymentType").val("EXISTING_LCAG_MEMBER");
            }

            function showPaymentFieldsSection() {
                $("#lcagUsernameSection").hide();
                $("#paymentFieldsSection").show();
                $("#newLcagJoinerSection").hide();
                $("#newLcagJoinerInfoSection").hide();
                $("#username").removeAttr("required");
                $("#emailAddress").removeAttr("required");
                $("#submitButton").show();
                $("#contributionTypeSection").show();
                $("#paymentType").val("ANONYMOUS");
            }

            function showNewLcagJoinerSection() {
                $("#lcagUsernameSection").hide();
                $("#newLcagJoinerSection").show();
                $("#newLcagJoinerInfoSection").show();
                $("#paymentFieldsSection").show();
                $("#username").removeAttr("required");
                $("#emailAddress").prop("required", true);
                $("#submitButton").show();
                $("#contributionTypeSection").show();
                $("#paymentType").val("NEW_LCAG_MEMBER");
            }

            $(function () {
                $("input[name=contributionTypeRadio]").change(function() {
                    $("#contributionAgreementMinimumAmountGbp").text(lcag.Common.config.contributionAgreementMinimumAmountGbp);
                    $("#payment-form").validate();
                    if ($("#existingLcagAccountAnonymous").prop("checked") || this.value == 'DONATION') {
                        $("#grossAmount").rules("remove");
                        $("#grossAmount").rules("add", {
                            required: true,
                            currency: true
                        });
                        $("#contributionAgreementInfoSection").hide();
                        $("#donationInfoSection").show();
                        $("#payment-form").validate().element("input[name=contributionTypeRadio]");
                    } else {
                        $("#grossAmount").rules("add", { min: lcag.Common.config.contributionAgreementMinimumAmountGbp });
                        $("#contributionAgreementInfoSection").show();
                        $("#donationInfoSection").hide();
                    }

                    if ($("input[name=grossAmount]").val() != null && $("input[name=grossAmount]").val() != "") {
                        $("#payment-form").validate().element("input[name=grossAmount]");
                    }
                });

                 $("#acceptTermsAndConditions").click(function() {
                    $("#paymentFormSection").show();
                    $("#acceptTermsAndConditions").attr("disabled", "disabled");
                    document.querySelector('#paymentFormSection').scrollIntoView({
                        behavior: 'smooth'
                    });
                });

                $("input[type=radio][name=existingLcagAccount]").change(function() {
                    $("#payment-form").validate().element("input[name=contributionTypeRadio]");

                    if (this.value == 'yes') {
                        $("input[name=contributionTypeRadio]").attr("disabled", false);
                        showLcagUsernameSection();
                    } else if (this.value == 'anonymous') {
                        $("#contributionTypeDonation").prop("checked", true).change();
                        $("#contributionTypeContributionAgreement").prop("checked", false).change();
                        $("input[name=contributionTypeRadio]").attr("disabled", true);
                        showPaymentFieldsSection();
                    } else {
                        $("input[name=contributionTypeRadio]").attr("disabled", false);
                        showNewLcagJoinerSection();
                    }

                    document.querySelector('#contributionTypeSection').scrollIntoView({
                        behavior: 'smooth'
                    });
                });

                if (getParameterByName('guid') != null) {
                    $.ajax({
                        url: '/payment?guid=' + getParameterByName('guid'),
                        method: "GET",
                        dataType: "json",
                        complete: function(response, status) {
                            if (status == "success") {
                                payment = response.responseJSON;

                                console.log("payment", payment);

                                if (payment.paymentStatus == "DECLINED" || payment.paymentStatus == "UNKNOWN_ERROR") {
                                    $("#paymentDeclinedSection").show();
                                    $("#paymentDeclinedErrorMessage").text(payment.errorDescription);
                                }

                                if (payment.paymentStatus == "VALIDATION_ERROR") {
                                    $("#validationErrorSection").show();
                                    $("#validationErrorMessage").text(payment.errorDescription);
                                }
                            }
                        }
                    });
                }

                $("#payment-form").validate({
                    rules: {
                        grossAmount: {
                            required: true,
                            currency: true
                        },
                        username: {
                            required: true,
                            lcagUsername: true
                        }
                    },
                    errorElement: "em",
                    errorPlacement: function ( error, element ) {
                        error.addClass( "help-block" );
                        element.parents( ".form-group" ).addClass( "has-feedback" );

                        if ($(element).attr('type') == 'radio') {
                            error.insertAfter( $(element).parent().parent() );
                        } else {
                            error.insertAfter( $(element).parent() );
                        }

                        if ( !element.next( "span" )[ 0 ] ) {
                            if ($(element).attr('type') == 'radio') {
                                $( "<span class='glyphicon glyphicon-remove form-control-feedback'></span>" ).insertAfter( $(element).parent().parent() );
                            } else {
                                $( "<span class='glyphicon glyphicon-remove form-control-feedback'></span>" ).insertAfter( $(element) );
                            }
                        }
                    },
                    success: function ( label, element ) {
                        if ($(element).attr('type') == 'radio') {
                            if ( !$( element ).parent().parent().next( "span" )[ 0 ] ) {
                                $( "<span class='glyphicon glyphicon-ok form-control-feedback'></span>" ).insertAfter( $(element).parent().parent() );
                            }
                        } else {
                            if ( !$( element ).next( "span" )[ 0 ] ) {
                                $( "<span class='glyphicon glyphicon-ok form-control-feedback'></span>" ).insertAfter( $(element) );
                            }
                        }
                    },
                    highlight: function ( element, errorClass, validClass ) {
                        $( element ).parents( ".form-group" ).addClass( "has-error" ).removeClass( "has-success" );
                        if ($(element).attr('type') == 'radio') {
                            $( element ).parent().parent().next( "span" ).addClass( "glyphicon-remove" ).removeClass( "glyphicon-ok" );
                        } else {
                            $( element ).next( "span" ).addClass( "glyphicon-remove" ).removeClass( "glyphicon-ok" );
                        }
                    },
                    unhighlight: function ( element, errorClass, validClass ) {
                        $( element ).parents( ".form-group" ).addClass( "has-success" ).removeClass( "has-error" );
                        if ($(element).attr('type') == 'radio') {
                            console.log("element", $(element));
                            $( element ).parent().parent().next( "span" ).addClass( "glyphicon-ok" ).removeClass( "glyphicon-remove" );
                        } else {
                            $( element ).next( "span" ).addClass( "glyphicon-ok" ).removeClass( "glyphicon-remove" );
                        }
                    }
                });
            });
        </script>

    </body>
</html>