function cleanForm() {
    $("#username").val("");
    $("#password").val("");
}

function showError(errorMessage) {
    $("#errorText").html(errorMessage);
    $("#errorModal").modal('show');
}

function doLogin(yalsUser, yalsPass) {
    console.log("In JS Home " + authApiPath);
    var loginRoute = "/api/login";

    var yalsUsername = yalsUser.trim();
    var yalsPassword = yalsPass.trim();

    var json = {
        yals_username: yalsUsername,
        yals_password: yalsPassword
    };
    doPost(loginRoute, json, onSuccessLogin, onFailLogin);
}


function onSuccessLogin(data, textStatus, jqXHR) {
    cleanForm();
    if (jqXHR.status === 200) {
        var token = data.token;
        if (token === undefined || token.trim().length === 0) {
            showError("Internal error. Got malformed reply from server");
        } else {
            //TODO add token to someplace
            window.location = window.location.origin + "/";
        }
    } else {
        onFailLogin(jqXHR, textStatus, "Successful answer, but HTTP status isn't 200");
    }
}

function onFailLogin(jqXHR, textStatus, errorThrown) {
    //debug symbols
    console.debug("jqXHR: " + jqXHR);
    console.debug("Text Status: " + textStatus);
    console.debug("Error thrown: " + errorThrown);

    if (jqXHR !== undefined && jqXHR !== null) {

        var replyRaw = jqXHR.responseText;
        console.debug("Reply JSON: " + replyRaw);
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
            if (reply.status !== undefined && reply.status === 404) {
                errors.push("Server responded with " + reply.status + " (" + reply.error + "), path: " + reply.path);
            } else {
                errors.push(reply.error.field + " " + reply.error.errorMessage);
            }

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


function handleForm(e) {
    e.preventDefault();

    var isFormValid = true;
    var yalsUsername = $("#username").val();
    var yalsPassword = $("#password").val();
    cleanForm();
    var errorMessage;
    if (yalsUsername === undefined || yalsUsername.trim().length === 0) {
        errorMessage = "Username cannot be empty";
        showError(errorMessage);
        isFormValid = false;
    }
    if (yalsPassword === undefined || yalsPassword.trim().length === 0) {
        errorMessage = "Password cannot be empty";
        showError(errorMessage);
        isFormValid = false;
    }

    if (isFormValid) {
        doLogin(yalsUsername, yalsPassword);
    }

}

$(document).ready(function () {
    $("#loginButton").on('click', handleForm);
});