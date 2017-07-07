/**
 * Makes Get request
 * @param url URL
 * @param successCallback function triggered when request is successfully proceeded
 * @param failCallback  function triggered is case of failed request
 */
function doGet(url,successCallback,failCallback){
    makeRequestWithoutPayload("GET",url,successCallback,failCallback);
}
/**
 * Makes POST request
 *
 * @param url Where to send
 * @param payload What to send
 * @param successCallback function triggered when request is successfully proceeded
 * @param failCallback  function triggered is case of failed request
 */
function doPost(url,payload,successCallback,failCallback){
    makePayloadedRequest("POST",url,payload,successCallback,failCallback);
}

/**
 * Makes PUT request
 *
 * @param url Where to send
 * @param payload What to send
 * @param successCallback function triggered when request is successfully proceeded
 * @param failCallback  function triggered is case of failed request
 */
function doPut(url,payload,successCallback,failCallback){
    makePayloadedRequest("PUT",url,payload,successCallback,failCallback);
}

/**
 * Makes DELETE request
 *
 * @param url Where to send
 * @param payload What to send
 * @param successCallback function triggered when request is successfully proceeded
 * @param failCallback  function triggered is case of failed request
 */
function doDelete(url,payload,successCallback,failCallback){
    makePayloadedRequest("DELETE",url,payload,successCallback,failCallback);
}

/**
 * Makes HEAD request
 *
 * @param url Where to send
 * @param successCallback function triggered when request is successfully proceeded
 * @param failCallback  function triggered is case of failed request
 */
function doHead(url,successCallback,failCallback){
    makeRequestWithoutPayload("HEAD",url,successCallback,failCallback);
}

/**
 * Makes Request with payload
 *
 * @param type POST,PUT,DELETE
 * @param url URL
 * @param payload What to send
 * @param successCallback function triggered when request is successfully proceeded
 * @param failCallback  function triggered is case of failed request
 */
function makePayloadedRequest(type,url,payload,successCallback,failCallback){

    $.ajax({
        type: type,
        url: url,
        data: JSON.stringify(payload),
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        async: false,
        success: function(data,textStatus,jqXHR){
            successCallback(data,textStatus,jqXHR);
        },
        error: function(jqXHR,textStatus,errorThrown){
            failCallback(jqXHR,textStatus,errorThrown);
        }
    });
}
/**
 * Makes Request with no payload
 *
 * @param type request method GET, HEAD
 * @param url Where to send
 * @param successCallback function triggered when request is successfully proceeded
 * @param failCallback  function triggered is case of failed request
 */
function makeRequestWithoutPayload(type,url,successCallback,failCallback){
    $.ajax({
        type: type,
        url: url,
        dataType: "json",
        async: false,
        success: function(data,textStatus,jqXHR){
            successCallback(data,textStatus,jqXHR);
        },
        error: function(jqXHR,textStatus,errorThrown){
            failCallback(jqXHR,textStatus,errorThrown);
        }
    });
}
