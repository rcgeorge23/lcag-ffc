var lcag = lcag || {};

lcag.Stripe = lcag.Stripe || {
    init: function(publishableStripeApiKey) {

        var stripe = Stripe(publishableStripeApiKey);

        var options = {
            success: function(response) {
                console.log("response", response);

                if (response != null && response != "") {
                    stripe.redirectToCheckout({
                        sessionId: response
                    }).then(function (result) {
                        alert("Error occurred: " + result.error.message);
                    });
                }
            }
        };

        $('#payment-form').ajaxForm(options);
    }
}