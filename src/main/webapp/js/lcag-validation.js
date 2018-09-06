var lcag = lcag || {};

lcag.Validation = lcag.Validation || {
    showPaymentSection: function () {
        $("#paymentFieldsSection").show();
        $("#submitButton").show();
        $("#contributionTypeSection").show();
    },
    hidePaymentSection: function () {
        $("#paymentFieldsSection").hide();
        $("#submitButton").hide();
    },
    hideVatSection: function() {
        $("#vatSection").hide();
        $("#contributorIsVatRegistered").removeAttr("required");
        $("#contributorIsVatRegisteredYes").prop("checked", false);
        $("#contributorIsVatRegisteredNo").prop("checked", false);
    },
    hideContributionTypeSection: function() {
        $("#contributionTypeSection").hide();
        lcag.Validation.enableAndUnsetContributionTypeRadio();
    },
    showContributionTypeSection: function() {
        $("#contributionTypeSection").show();
    },
    showVatSection: function() {
        $("#vatSection").show();
        $("#contributorIsVatRegistered").prop("required", true);
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
    showCompanyNameSection: function() {
        $("#companyNameSection").show();
        $("#companyNameSection").prop("required", true);
    },
    hideCompanyNameSection: function() {
        $("#companyNameSection").hide();
        $("#companyNameSection").removeAttr("required");
    },
    selectAndDisableContributionTypeDonation() {
        $("#contributionTypeDonation").prop("checked", true);
        $("#contributionTypeContributionAgreement").prop("checked", false);
        $("input[name=contributionTypeRadio]").attr("disabled", true);
    },
    displayFieldsAndSetupValidationRules: function() {
        console.log("displayFieldsAndSetupValidationRules");
        if (lcag.Validation.paymentTypeRadio() == "ANONYMOUS") {
            $("#paymentType").val("ANONYMOUS");
            lcag.Validation.hideVatSection();
            lcag.Validation.hideAddressSection();
            lcag.Validation.hideUsernameSection();
            lcag.Validation.hideNameSection();
            lcag.Validation.hideCompanyNameSection();
            lcag.Validation.selectAndDisableContributionTypeDonation();
            $("#newLcagJoinerInfoSection").hide();
            lcag.Validation.showPaymentSection();
            console.log("scrolling to donation section")
            $("#donationInfoSection").show();
            $("#contributionAgreementInfoSection").hide();
            document.querySelector('#donationInfoSection').scrollIntoView({
                behavior: 'smooth'
            });
        } else if (lcag.Validation.existingLcagMemberRadioYesOrNoSelectedAfterAnonymous()) {
            lcag.Validation.enableAndUnsetContributionTypeRadio();
            lcag.Validation.showContributionTypeSection();
            lcag.Validation.hideVatSection();
            lcag.Validation.hideAddressSection();
            lcag.Validation.hideUsernameSection();
            lcag.Validation.hidePaymentSection();
        } else if (lcag.Validation.paymentTypeRadio() == "NEW_LCAG_MEMBER" && lcag.Validation.contributorIsVatRegisteredRadio() == null && lcag.Validation.contributionTypeRadio() == "DONATION") {
            lcag.Validation.hideUsernameSection();
            lcag.Validation.hideCompanyNameSection();
            lcag.Validation.showNameSection();
            lcag.Validation.showPaymentSection();
        } else if (lcag.Validation.paymentTypeRadio() == "NEW_LCAG_MEMBER" && lcag.Validation.contributorIsVatRegisteredRadio() == null && lcag.Validation.contributionTypeRadio() == "CONTRIBUTION_AGREEMENT") {
            lcag.Validation.hideUsernameSection();
            lcag.Validation.hideCompanyNameSection();
            lcag.Validation.showNameSection();
            lcag.Validation.showAddressSection();
            lcag.Validation.showPaymentSection();
        } else if (lcag.Validation.paymentTypeRadio() == "EXISTING_LCAG_MEMBER" && lcag.Validation.contributorIsVatRegisteredRadio() == null && lcag.Validation.contributionTypeRadio() == "DONATION") {
            lcag.Validation.showUsernameSection();
            lcag.Validation.hideCompanyNameSection();
            lcag.Validation.hideNameSection();
            lcag.Validation.showPaymentSection();
            lcag.Validation.hideAddressSection();
        } else if (lcag.Validation.paymentTypeRadio() == "EXISTING_LCAG_MEMBER" && lcag.Validation.contributorIsVatRegisteredRadio() == null && lcag.Validation.contributionTypeRadio() == "CONTRIBUTION_AGREEMENT") {
            lcag.Validation.hideUsernameSection();
            lcag.Validation.hideCompanyNameSection();
            lcag.Validation.hideNameSection();
            lcag.Validation.hidePaymentSection();
            lcag.Validation.hideAddressSection();
            lcag.Validation.showVatSection();
        } else if (lcag.Validation.paymentTypeRadio() == "EXISTING_LCAG_MEMBER" && lcag.Validation.contributorIsVatRegisteredRadio() == true && lcag.Validation.contributionTypeRadio() == "CONTRIBUTION_AGREEMENT") {
            lcag.Validation.showUsernameSection();
            lcag.Validation.showCompanyNameSection();
            lcag.Validation.showPaymentSection();
            lcag.Validation.showNameSection();
            lcag.Validation.showAddressSection();
            $("#donationInfoSection").hide();
            $("#contributionAgreementInfoSection").show();
            document.querySelector('#contributionAgreementInfoSection').scrollIntoView({
                behavior: 'smooth'
            });
        } else if (lcag.Validation.paymentTypeRadio() == "EXISTING_LCAG_MEMBER" && lcag.Validation.contributorIsVatRegisteredRadio() == false && lcag.Validation.contributionTypeRadio() == "CONTRIBUTION_AGREEMENT") {
            lcag.Validation.showUsernameSection();
            lcag.Validation.hideCompanyNameSection();
            lcag.Validation.showPaymentSection();
            lcag.Validation.showNameSection();
            lcag.Validation.showAddressSection();
            $("#donationInfoSection").hide();
            $("#contributionAgreementInfoSection").show();
            document.querySelector('#contributionAgreementInfoSection').scrollIntoView({
                behavior: 'smooth'
            });
        } else if (lcag.Validation.paymentTypeRadio() == "NEW_LCAG_MEMBER" && lcag.Validation.contributorIsVatRegisteredRadio() == false && lcag.Validation.contributionTypeRadio() == "CONTRIBUTION_AGREEMENT") {
            lcag.Validation.hideUsernameSection();
            lcag.Validation.hideCompanyNameSection();
            lcag.Validation.showPaymentSection();
            lcag.Validation.showNameSection();
            lcag.Validation.showAddressSection();
            $("#donationInfoSection").hide();
            $("#contributionAgreementInfoSection").show();
            document.querySelector('#contributionAgreementInfoSection').scrollIntoView({
                behavior: 'smooth'
            });
        } else if (lcag.Validation.paymentTypeRadio() == "NEW_LCAG_MEMBER" && lcag.Validation.contributorIsVatRegisteredRadio() == false && lcag.Validation.contributionTypeRadio() == "DONATION") {
            lcag.Validation.hideUsernameSection();
            lcag.Validation.hideCompanyNameSection();
            lcag.Validation.showPaymentSection();
            lcag.Validation.showNameSection();
            lcag.Validation.hideAddressSection();
            $("#donationInfoSection").show();
            $("#contributionAgreementInfoSection").hide();
            document.querySelector('#donationInfoSection').scrollIntoView({
                behavior: 'smooth'
            });
        } else if (lcag.Validation.newOrExistingLcagMember() && lcag.Validation.contributorIsVatRegisteredRadio() == null) {
            lcag.Validation.showContributionTypeSection();
        } else if (lcag.Validation.newOrExistingLcagMember() && lcag.Validation.contributorIsVatRegisteredRadio() != null) {
            lcag.Validation.showContributionTypeSection();
        }

        if (lcag.Validation.contributionTypeRadio() == "CONTRIBUTION_AGREEMENT") {
            $("#grossAmount").rules("remove");
            $("#grossAmount").rules("add", {
                required: true,
                currency: true,
                min: parseInt(lcag.Common.config.contributionAgreementMinimumAmountGbp)
            });
            lcag.Validation.showVatSection();
            $("#contributionAgreementInfoSection").show();
            $("#donationInfoSection").hide();
        } else if (lcag.Validation.contributionTypeRadio() == "DONATION") {
            $("#grossAmount").rules("remove");
            $("#grossAmount").rules("add", {
                required: true,
                currency: true,
                min: 1
            });
            lcag.Validation.hideVatSection();
            $("#donationInfoSection").show();
            $("#contributionAgreementInfoSection").hide();
        } else {
            $("#donationInfoSection").hide();
            $("#contributionAgreementInfoSection").hide();
        }

        if (lcag.Validation.contributorIsVatRegisteredRadio() == true) {
            lcag.Validation.showCompanyNameSection();
        } else {
            lcag.Validation.hideCompanyNameSection();
        }

        if (lcag.Validation.paymentTypeRadio() == "NEW_LCAG_MEMBER") {
            lcag.Validation.hideUsernameSection();
        }

        $("#contributionType").val(lcag.Validation.contributionTypeRadio());
        $("#paymentType").val(lcag.Validation.paymentTypeRadio());
    },
    init: function() {
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
    },
    newOrExistingLcagMember: function() {
        return lcag.Validation.paymentTypeRadio() == "NEW_LCAG_MEMBER" || lcag.Validation.paymentTypeRadio() == "EXISTING_LCAG_MEMBER";
    },
    existingLcagMemberRadioYesOrNoSelectedAfterAnonymous: function() {
        return lcag.Validation.paymentTypeRadio() != null && lcag.Validation.contributorIsVatRegisteredRadio() == null && lcag.Validation.contributionTypeRadio() == "DONATION" && lcag.Validation.contributionTypeRadioIsDisabled()
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
    contributorIsVatRegisteredRadio: function() {
        if ($("#contributorIsVatRegisteredYes").prop("checked")) {
            return true;
        } else if ($("#contributorIsVatRegisteredNo").prop("checked")) {
            return false;
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
    contributionTypeRadioIsDisabled: function() {
        return $("input[name=contributionTypeRadio]").attr("disabled") == "disabled";
    },
    enableAndUnsetContributionTypeRadio: function() {
        $("input[name=contributionTypeRadio]").attr("disabled", false);
        $("#contributionTypeDonation").prop("checked", false);
        $("#contributionTypeDonationContributionAgreement").prop("checked", false);
    },
    acceptTermsAndConditions: function() {
        $("#paymentFormSection").show();
        $("#acceptTermsAndConditions").attr("disabled", "disabled");
        document.querySelector('#paymentFormSection').scrollIntoView({
            behavior: 'smooth'
        });
    }
}