var lcag = lcag || {};

lcag.Validation = lcag.Validation || {
    config: {
        contributionAgreementMinimumAmountGbp: null
    },
    showPaymentSection: function () {
        $("#paymentFieldsSection").show();
        $("#submitButton").show();
        $("#contributionTypeSection").show();
    },
    hidePaymentSection: function () {
        $("#paymentFieldsSection").hide();
        $("#submitButton").hide();
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
    updateOtherGrossAmountValues: function() {
        $(".gross-amount").text(lcag.Validation.formatMoney(lcag.Validation.grossAmount()));
    },
    displayFieldsAndSetupValidationRules: function() {
        if (lcag.Validation.grossAmount() == null || lcag.Validation.grossAmount() < 1) {
            $("#existingLcagAccountSection").hide();
            $("#contributionAgreementInfoSection").hide();
            $("#donationInfoSection").hide();
        } else if (lcag.Validation.grossAmount() < lcag.Validation.config.contributionAgreementMinimumAmountGbp) {
            $("#existingLcagAccountSection").show();
            $("#contributionAgreementInfoSection").hide();
            $("#donationInfoSection").show();
        } else if (lcag.Validation.grossAmount() >= lcag.Validation.config.contributionAgreementMinimumAmountGbp) {
            $("#existingLcagAccountSection").show();
            $("#contributionAgreementInfoSection").show();
            $("#donationInfoSection").hide();
        }

        if (lcag.Validation.paymentTypeRadio() == "NEW_LCAG_MEMBER") {
            lcag.Validation.hideUsernameSection();
            lcag.Validation.showNameSection();
            lcag.Validation.showAddressSection();
            lcag.Validation.showPaymentSection();
        } else if (lcag.Validation.paymentTypeRadio() == "EXISTING_LCAG_MEMBER") {
            lcag.Validation.showUsernameSection();
            lcag.Validation.showPaymentSection();
            lcag.Validation.showNameSection();
            lcag.Validation.showAddressSection();
            $("#newLcagJoinerInfoSection").hide();
            document.querySelector('#contributionAgreementInfoSection').scrollIntoView({
                behavior: 'smooth'
            });
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
    enableAndUnsetContributionTypeRadio: function() {
        $("input[name=contributionTypeRadio]").attr("disabled", false);
        $("#contributionTypeDonation").prop("checked", false);
        $("#contributionTypeDonationContributionAgreement").prop("checked", false);
    },
    grossAmount: function() {
        return $("#grossAmount").val() == null ? null : parseFloat($("#grossAmount").val());
    },
    acceptTermsAndConditions: function() {
        $("#paymentFormSection").show();
        $("#acceptTermsAndConditions").attr("disabled", "disabled");
        document.querySelector('#paymentFormSection').scrollIntoView({
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