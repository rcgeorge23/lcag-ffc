var lcag = lcag || {};

lcag.Validation = lcag.Validation || {
    config: {
        contributionAgreementMinimumAmountGbp: null
    },
    state: {
        hasConfirmedContributionDetails: false
    },
    showPaymentSection: function () {
        $("#paymentFieldsSection").show();
        $("#confirmButton").show();
        $("#contributionTypeSection").show();
    },
    hidePaymentSection: function () {
        $("#paymentFieldsSection").hide();
        $("#confirmButton").hide();
    },
    hideContributionTypeSection: function() {
        $("#contributionTypeSection").hide();
        lcag.Validation.enableAndUnsetContributionTypeRadio();
    },
    showContributionTypeSection: function() {
        $("#contributionTypeSection").show();
    },
    showAddressSection: function() {
        $("#addressSection").show();
        $("#addressLine1").prop("required", true);
        $("#city").prop("required", true);
        $("#postalCode").prop("required", true);
        $("#country").prop("required", true);
    },
    hideAddressSection: function() {
        $("#addressSection").hide();
        $("#addressLine1").removeAttr("required");
        $("#city").removeAttr("required");
        $("#postalCode").removeAttr("required");
        $("#country").removeAttr("required");
    },
    hideNameSection: function() {
        $("#nameSection").hide();
        $("#firstName").removeAttr("required");
        $("#lastName").removeAttr("required");
        $("#emailAddress").removeAttr("required");
    },
    showNameSection: function() {
        $("#nameSection").show();
        $("#firstName").prop("required", true);
        $("#lastName").prop("required", true);
        $("#emailAddress").prop("required", true);
    },
    hideUsernameSection: function() {
        $("#lcagUsernameSection").hide();
        $("#lcagUsernameSection").removeAttr("required");
    },
    showUsernameSection: function() {
        $("#lcagUsernameSection").show();
        $("#lcagUsernameSection").prop("required", true);
    },
    selectAndDisableContributionTypeDonation() {
        $("#contributionTypeDonation").prop("checked", true);
        $("#contributionTypeContributionAgreement").prop("checked", false);
        $("input[name=contributionTypeRadio]").attr("disabled", true);
    },
    unselectContributionType() {
        $("#contributionTypeContributionAgreement").prop("checked", false);
        $("#contributionTypeDonation").prop("checked", false);
    },
    updateOtherGrossAmountValues: function() {
        $(".gross-amount").text(lcag.Validation.formatMoney(lcag.Validation.grossAmount()));
    },
    updateVisibilityOfConfirmationSection: function() {
        var validate = $("#payment-form").validate();

        console.log("lcag.Validation.state.hasConfirmedContributionDetails:", lcag.Validation.state.hasConfirmedContributionDetails);
        console.log("validate.checkForm():", validate.checkForm());

        if (lcag.Validation.state.hasConfirmedContributionDetails == true && validate.checkForm()) {
            $("#confirmButton").show();
            $("#confirmationSection").show();
            $("#confirmButton").attr("disabled", "disabled");
            return true;
        } else {
//            $("#confirmButton").hide();
            lcag.Validation.state.hasConfirmedContributionDetails = false;
            $("#confirmationSection").hide();
            $("#confirmButton").prop("disabled", false);
            document.querySelector('#contributionDetailsSection').scrollIntoView({
                behavior: 'smooth'
            });
            return false;
        }
        validate.submitted = {};
    },
    displayFieldsAndSetupValidationRules: function() {
        lcag.Validation.state.hasConfirmedContributionDetails = false;
        if (lcag.Validation.grossAmount() == null || lcag.Validation.grossAmount() < 1) {
            $("#contributionType").val("DONATION");
            lcag.Validation.unselectContributionType();
            $("#existingLcagAccountSection").hide();
            $("#contributionAgreementInfoSection").hide();
            $("#donationInfoSection").hide();
        } else if (lcag.Validation.grossAmount() < lcag.Validation.config.contributionAgreementMinimumAmountGbp) {
            $("#contributionType").val("DONATION");
            lcag.Validation.unselectContributionType();
            $("#existingLcagAccountSection").show();
            $("#contributionAgreementInfoSection").hide();
            $("#donationInfoSection").show();
        } else if (lcag.Validation.grossAmount() >= lcag.Validation.config.contributionAgreementMinimumAmountGbp && lcag.Validation.contributionTypeRadio() == null) {
            $("#existingLcagAccountSection").hide();
            $("#contributionAgreementInfoSection").show();
            $("#donationInfoSection").hide();
        } else if (lcag.Validation.grossAmount() >= lcag.Validation.config.contributionAgreementMinimumAmountGbp && lcag.Validation.contributionTypeRadio() != null) {
            $("#contributionType").val(lcag.Validation.contributionTypeRadio());
            $("#existingLcagAccountSection").show();
            $("#contributionAgreementInfoSection").show();
            $("#donationInfoSection").hide();
        }

        if (lcag.Validation.paymentTypeRadio() == "ANONYMOUS") {
            $("#newLcagJoinerInfoSection").hide();
            lcag.Validation.hideAddressSection();
            lcag.Validation.hideUsernameSection();
            lcag.Validation.hideNameSection();
            lcag.Validation.showPaymentSection();
            document.querySelector('#donationInfoSection').scrollIntoView({
                behavior: 'smooth'
            });
        } else if (lcag.Validation.paymentTypeRadio() == "NEW_LCAG_MEMBER" && lcag.Validation.contributionTypeField() == "DONATION") {
            lcag.Validation.hideUsernameSection();
            lcag.Validation.showNameSection();
            lcag.Validation.showPaymentSection();
            lcag.Validation.hideAddressSection();
        } else if (lcag.Validation.paymentTypeRadio() == "NEW_LCAG_MEMBER" && lcag.Validation.contributionTypeField() == "CONTRIBUTION_AGREEMENT") {
            lcag.Validation.hideUsernameSection();
            lcag.Validation.showNameSection();
            lcag.Validation.showAddressSection();
            lcag.Validation.showPaymentSection();
        } else if (lcag.Validation.paymentTypeRadio() == "EXISTING_LCAG_MEMBER" && lcag.Validation.contributionTypeField() == "DONATION") {
            lcag.Validation.showUsernameSection();
            lcag.Validation.hideNameSection();
            lcag.Validation.showPaymentSection();
            lcag.Validation.hideAddressSection();
        } else if (lcag.Validation.paymentTypeRadio() == "EXISTING_LCAG_MEMBER" && lcag.Validation.contributionTypeRadio() == "CONTRIBUTION_AGREEMENT") {
            lcag.Validation.showUsernameSection();
            lcag.Validation.showPaymentSection();
            lcag.Validation.showNameSection();
            lcag.Validation.showAddressSection();
            $("#newLcagJoinerInfoSection").hide();
            $("#donationInfoSection").hide();
            $("#contributionAgreementInfoSection").show();
            document.querySelector('#contributionAgreementInfoSection').scrollIntoView({
                behavior: 'smooth'
            });
        }

        if (lcag.Validation.contributionTypeField() == "CONTRIBUTION_AGREEMENT") {
            $("#existingLcagAccountAnonymous").attr("disabled", true);
            $("#existingLcagAccountAnonymous").prop("checked", false);
        } else {
            $("#existingLcagAccountAnonymous").attr("disabled", false);
        }

        if (lcag.Validation.paymentTypeRadio() == null) {
            lcag.Validation.hideUsernameSection();
            lcag.Validation.hideNameSection();
            lcag.Validation.hideAddressSection();
            lcag.Validation.hidePaymentSection();
        }

        $("#paymentType").val(lcag.Validation.paymentTypeRadio());
    },
    init: function(contributionAgreementMinimumAmountGbp) {
        lcag.Validation.config.contributionAgreementMinimumAmountGbp = parseInt(contributionAgreementMinimumAmountGbp);

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

        $("#payment-form").validate({
            rules: {
                grossAmount: {
                    required: true,
                    currency: true,
                    min: 1,
                    max: 999999.99
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
    },
    newOrExistingLcagMember: function() {
        return lcag.Validation.paymentTypeRadio() == "NEW_LCAG_MEMBER" || lcag.Validation.paymentTypeRadio() == "EXISTING_LCAG_MEMBER";
    },
    existingLcagMemberRadioYesOrNoSelectedAfterAnonymous: function() {
        return lcag.Validation.paymentTypeRadio() != null && lcag.Validation.contributionTypeRadio() == "DONATION" && lcag.Validation.contributionTypeRadioIsDisabled()
    },
    paymentTypeRadio: function() {
        if ($("#existingLcagAccountAnonymous").prop("checked")) {
            return "ANONYMOUS";
        } else if ($("#existingLcagAccountNo").prop("checked")) {
            return "NEW_LCAG_MEMBER";
        } else if ($("#existingLcagAccountYes").prop("checked")) {
            return "EXISTING_LCAG_MEMBER";
        }

        return null;
    },
    contributionTypeRadio: function() {
        if ($("#contributionTypeDonation").prop("checked")) {
            return "DONATION";
        } else if ($("#contributionTypeContributionAgreement").prop("checked")) {
            return "CONTRIBUTION_AGREEMENT";
        }

        return null;
    },
    contributionTypeField: function() {
        return $("#contributionType").val();
    },
    contributionTypeRadioIsDisabled: function() {
        return $("input[name=contributionTypeRadio]").attr("disabled") == "disabled";
    },
    enableAndUnsetContributionTypeRadio: function() {
        $("input[name=contributionTypeRadio]").attr("disabled", false);
        $("#contributionTypeDonation").prop("checked", false);
        $("#contributionTypeDonationContributionAgreement").prop("checked", false);
    },
    grossAmount: function() {
        return $("#grossAmount").val() == null ? null : parseFloat($("#grossAmount").val());
    },
    confirmContributionDetails: function() {
        if ($("#payment-form").valid()) {
            lcag.Validation.state.hasConfirmedContributionDetails = true;
            if (lcag.Validation.updateVisibilityOfConfirmationSection()) {
                document.querySelector('#confirmationSection').scrollIntoView({
                    behavior: 'smooth'
                });
            }
        }
    },
    acceptTermsAndConditions: function() {
        $("#contributionDetailsSection").show();
        $("#acceptTermsAndConditions").attr("disabled", "disabled");
        document.querySelector('#contributionDetailsSection').scrollIntoView({
            behavior: 'smooth'
        });
    },
    formatMoney: function(n, c, d, t) {
        var c = isNaN(c = Math.abs(c)) ? 2 : c,
        d = d == undefined ? "." : d,
        t = t == undefined ? "," : t,
        s = n < 0 ? "-" : "",
        i = String(parseInt(n = Math.abs(Number(n) || 0).toFixed(c))),
        j = (j = i.length) > 3 ? j % 3 : 0;
        return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
    }
}