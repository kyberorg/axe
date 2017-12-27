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
<div class="container-fluid">
    <div id="first" class="row">
        <div class="col-xs-0 col-sm-1 col-md-3 col-lg-3"></div>
        <div id="error" class="alert alert-danger col-xs-12 col-sm-10 col-md-6 col-lg-6 invisible">
            <button id="errorClose" class="close" type="button" aria-hidden="true">&times;</button>
            <strong>Hups! </strong><span id="errorText"></span>
        </div>
        <div class="col-xs-0 col-sm-1 col-md-3 col-lg-3"></div>
    </div>
    <!-- Main Div -->
    <div id="main" class="row">
        <div class="col-xs-0 col-sm-1 col-md-3 col-lg-3"></div>
        <div id="center" class="main col-xs-12 col-sm-10 col-md-6 col-lg-6">
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
        <div class="col-xs-0 col-sm-1 col-md-3 col-lg-3"></div>
    </div>
    <!-- Overall Div -->
    <div id="overallLinksDiv" class="row">
        <div class="col-xs-0 col-sm-1 col-md-3 col-lg-3"></div>
        <div id="overallLinks" class="main col-xs-12 col-sm-10 col-md-6 col-lg-6">
            <span id="overallLinksText">
                Yals already saved <span id="overallLinksNum">${params.overallLinks}</span> links
            </span>
        </div>
        <div class="col-xs-0 col-sm-1 col-md-3 col-lg-3"></div>
    </div>
    <!-- Result Div -->
    <div id="resultRow" class="row">
        <div class="col-xs-0 col-sm-1 col-md-3 col-lg-3"></div>
        <div id="result" class="result col-xs-12 col-sm-10 col-md-6 col-lg-6 invisible">
            <span class="strong-link">
                <a id="resultLink" href=""></a>
            </span>
            <span id="copyLink" class="glyphicon glyphicon-duplicate" aria-hidden="true"
                  data-toggle="tooltip" data-placement="right" title="Copy link"></span>
        </div>
        <div class="col-xs-0 col-sm-1 col-md-3 col-lg-3"></div>
    </div>
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

<!-- Link copied modal -->
<div class="modal fade bs-example-modal-sm" id="linkCopiedModal">
    <div class="modal-dialog modal-sm" role="document">
        <div class="modal-body modal-content alert alert-success">
            <span class="glyphicon glyphicon-ok-sign" aria-hidden="true"></span>
            <p class="in-same-line">Short link copied</p>
        </div><!-- /.modal-body -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

</body>
</html>
