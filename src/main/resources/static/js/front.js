function cleanErrors() {
    $("#errorText").html("");
    $("#errorModal").modal('hide');
}

function cleanForm() {
    $("#longUrl").val("");
}

function cleanResults() {
    $("#resultLink").html("").attr("href", "");
    $("#result").addClass('invisible');

    $("#qrCode img").attr('src', "");
    $("#qrCode").addClass('invisible');
}

function showError(errorMessage) {
    $("#errorText").html(errorMessage);
    $("#errorModal").modal('show');
}

function sendLink(long_url) {
    var storeLinkRoute = "/api/store";

    var longUrl = long_url.trim();
    var json = {
        link: longUrl
    };
    doPost(storeLinkRoute, json, onSuccessStoreLink, onFailStoreLink);
}

function onSuccessStoreLink(data, textStatus, jqXHR) {
    cleanErrors();
    cleanForm();
    if (jqXHR.status === 201) {
        var ident = data.ident;
        if (ident === undefined || ident.trim().length === 0) {
            showError("Internal error. Got malformed reply from server");
            return;
        }

        $("#resultLink").html(window.location.origin + "/" + ident).attr("href", ident);
        $("#result").removeClass('invisible');
        updateCounter();
        generateQRCode(ident);
    }
}

function onFailStoreLink(jqXHR, textStatus, errorThrown) {
    if (jqXHR !== null || jqXHR !== undefined) {

        var replyRaw = jqXHR.responseText;
        console.log("Reply JSON: " + replyRaw);
        var reply = JSON.parse(replyRaw);

        var errors = [];

        if (reply.errors !== undefined) {
            if (reply.errors.length > 0) {
                $.each(reply.errors, function (index, value) {
                    if (value.field === "link") {
                        value.field = "Long URL";
                    }
                    errors.push(value.field + " " + value.errorMessage);
                });
            }
        } else if (reply.error !== undefined) {
            errors.push(reply.error.field + " " + reply.error.errorMessage);
        }

        var errorText = "Validation failed <BR>";
        if (errors.length >= 0) {
            errorText += "Errors: <BR>";
            $.each(errors, function (index, value) {
                errorText += value + "<BR>";
            });
        }
        showError(errorText);
    }
}

function copyLinkToClipboard() {
    var $temp = $("<input>");
    $("body").append($temp);
    $temp.val($("#resultLink").text()).select();
    document.execCommand("copy");
    $temp.remove();

    $("#linkCopiedModal").modal('show');
    setTimeout(function () {
        $("#linkCopiedModal").modal('hide');
    }, 1000);
}

function updateCounter() {
    var counter = $("#overallLinksNum");
    var currentNum = counter.text();

    if ($.isNumeric(currentNum)) {
        counter.text(parseInt(currentNum) + 1);
    } else {
        console.error("Failed to update counter. Current counter value is not a number")
    }
}

function calculateQRCodeSize() {
    var w = $(window).width();

    var size;
    if (w > 371) {
        size = 350;
    } else {
        size = w * 0.943;
    }

    return size;
}

function generateQRCode(ident) {
    var size = calculateQRCodeSize();
    var qrCodeGeneratorRoute = "/api/qrCode/" + ident + "/" + parseInt(size);
    doGet(qrCodeGeneratorRoute, onSuccessGenerateQRCode, onFailGenerateQRCode);
}

function onSuccessGenerateQRCode(data, textStatus, jqXHR) {
    if (jqXHR.status === 200) {
        var qrCode = data.qrCode;
        if (qrCode === undefined || qrCode.trim().length === 0) {
            showError("Internal error. Got malformed reply from QR generator");
            return;
        }

        $("#qrCode img").attr('src', qrCode);
        $("#qrCode").removeClass('invisible');
    }
}

function onFailGenerateQRCode(jqXHR, textStatus, errorThrown) {
    //TODO do it better
    if (jqXHR !== null) {
        var replyRaw = jqXHR.responseText;
        console.debug("QR Code Reply JSON: " + replyRaw);
        console.debug("QR Code Reply TextStatus: " + textStatus);
        console.debug("QR Code Reply ErrorThrown: " + errorThrown);
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
    $('[data-toggle="tooltip"]').tooltip();
    $("#copyLink").on('click', copyLinkToClipboard);
});
