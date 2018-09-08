var lcag = lcag || {};

lcag.Common = lcag.Common || {
    config: {},
    urlPrefix: "",
    alertSuccess: function() {
        toastr.success("Updated successfully");
    },
    alertError: function() {
        toastr.error("An error occurred");
    }
}