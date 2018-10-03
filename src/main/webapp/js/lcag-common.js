var lcag = lcag || {};

lcag.Common = lcag.Common || {
    config: {},
    urlPrefix: "",
    alertSuccess: function() {
        toastr.success("Updated successfully", {
            "maxOpened": "1"
        });
    },
    alertError: function(message) {
        if (message != null && message != "") {
            toastr.error(message);
        } else {
            toastr.error("An error occurred");
        }
    }
}