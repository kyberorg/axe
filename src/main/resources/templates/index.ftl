<#import "lib/head.ftl" as head>
<#import "lib/js.ftl" as js>
<!DOCTYPE html>
<html lang="en">
<head>
<@head.meta></@head.meta>
    <title>Link shortener for friends</title>

<@head.css></@head.css>

<@head.donkey></@head.donkey>
</head>
<body>
<@js.jquery></@js.jquery>
<@js.jquery_bootstrap></@js.jquery_bootstrap>
<@js.request_js></@js.request_js>
<script src="/s/js/front.js"></script>

<!-- Error Div -->
<div id="first" class="row">
    <div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">&nbsp;</div>
    <div id="error" class="alert alert-danger col-xs-6 col-sm-6 col-md-6 col-lg-6 invisible">
        <button id="errorClose" class="close" type="button" aria-hidden="true">&times;</button>
        <strong>Hups! </strong><span id="errorText"></span>
    </div>
    <div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">&nbsp;</div>
</div>
<!-- Main Div -->
<div id="main" class="row">
    <div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">&nbsp;</div>
    <div id="center" class="main col-xs-6 col-sm-6 col-md-6 col-lg-6">
        <h2>Yet another link shortener</h2>
        <p class="lead"><i>... for friends</i></p>
        <form role="form">
            <div class="form-group">
                <label for="longUrl">Your very long URL here: </label>
                <input type="text" class="form-control" name="longUrl" id="longUrl"
                       placeholder="http://mysuperlongurlhere.tld">
                <p id="publicAccessBanner" class="help-block">
                    Note: all links considered as public and can be used by
                    anyone
                </p>
            </div>
            <button id="shortenIt" type="submit" class="btn btn-primary">Shorten it!</button>
        </form>
        <span id="emptyLine">&nbsp;</span>
    </div>
    <div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">&nbsp;</div>
</div>
<!-- Result Div -->
<div id="resultRow" class="row">
    <div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">&nbsp;</div>
    <div id="result" class="result col-xs-6 col-sm-6 col-md-6 col-lg-6 invisible">
            <span class="strong-link">
                <a id="resultLink" href=""></a>
            </span>
    </div>
    <div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">&nbsp;</div>
</div>

<#if (params.displayCommitInfo)>
<footer class="footer">
    <div class="container">
        <span id="version" class="text-muted">
            Version ${params.commitTag} (code based on commit
            <a href="${params.repository}/${params.commitHash}">
            ${params.commit}</a>)
        </span>
    </div>
</footer>
</#if >
</body>
</html>