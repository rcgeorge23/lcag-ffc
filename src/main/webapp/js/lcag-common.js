var lcag = lcag || {};

lcag.Common = lcag.Common || {
    config: {},
    urlPrefix: "",
    alertSuccess: function() {
        toastr.success("Updated successfully");
    },
    alertError: function() {
        toastr.error("An error occurred");
    },
    init: function() {
        $.ajax({
            url: "/config",
            async: false,
            dataType: 'json',
            success: function (result) {
                lcag.Common.config = result;
            }
        });
    }
}