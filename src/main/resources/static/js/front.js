function cleanErrors() {
    $("#errorText").html("");
    $("#error").addClass('invisible');
}

function cleanForm() {
    $("#longUrl").val("");
}

function cleanResults(){
    $("#resultLink").html("").attr("href","");
    $("#result").addClass('invisible');
}

function showError(errorMessage) {
    $("#errorText").html(errorMessage);
    $("#error").removeClass('invisible');
}

function sendLink(long_url) {
    var storeLinkRoute = "/api/store";

    var longUrl = long_url.trim();
    var json = {
        link : longUrl
    };
    doPost(storeLinkRoute, json, onSuccessStoreLink, onFailStoreLink);
}

function onAlertClose() {
    $("#errorText").html("");
    $("#error").addClass('invisible');
}

function onSuccessStoreLink(data, textStatus, jqXHR) {
    cleanErrors();
    cleanForm();
    if(jqXHR.status === 201) {
        var ident = data.ident;
        if(ident === undefined || ident.trim().length === 0) {
            showError("Internal error. Got malformed reply from server");
            return;
        }
        $("#resultLink").html(window.location.href + ident).attr("href", ident);
        $("#result").removeClass('invisible');
    }
}

function onFailStoreLink(jqXHR, textStatus, errorThrown) {
    if (jqXHR !== null || jqXHR != undefined) {

        var replyRaw = jqXHR.responseText;
        console.log("Reply JSON: " + replyRaw);
        var reply = JSON.parse(replyRaw);

        var errors = [];

        if (reply.errors !== undefined) {
            if (reply.errors.length > 0) {
                $.each(reply.errors, function (index, value) {
                    if(value.field === "link"){
                        value.field = "Long URL";
                    }
                    errors.push(value.field + " " + value.errorMessage);
                });
            }
        } else if (reply.error !== undefined) {
            errors.push(reply.error.field + " " + reply.error.errorMessage);
        }

        var errorText = "Validation failed <BR>";
        if(errors.length >= 0) {
            errorText += "Errors: <BR>";
            $.each(errors, function (index, value) {
                errorText += value + "<BR>";
            });
        }
        showError(errorText);
    }
}

function handleForm(e) {
    e.preventDefault();
    cleanErrors();
    cleanResults();

    var isFormValid = true;

    var longUrl = $("#longUrl").val();
    console.log("Got long URL: " + longUrl);
    cleanForm();
    if (longUrl === undefined || longUrl.trim().length === 0) {
        var errorMessage = "Long URL cannot be empty";
        showError(errorMessage);
        isFormValid = false;
    }
    if (isFormValid) {
        sendLink(longUrl);
    }
}

$(document).ready(function () {
    $("#shortenIt").on('click', handleForm);
    $("#errorClose").on('click', onAlertClose)
});
