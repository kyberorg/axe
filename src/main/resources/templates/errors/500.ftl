<#import "../lib/head.ftl" as head>
<#import "../lib/css.ftl" as css>
<!DOCTYPE html>
<html lang="en">
<head>
<@head.meta></@head.meta>
    <title>Error 500</title>
<@css.bs></@css.bs>
<@head.donkey></@head.donkey>
</head>
<body>
<div id="top" class="row">&nbsp;</div>
<div class="row">
    <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
        &nbsp;
    </div>
    <div class="col-xs-10 col-sm-10 col-md-10 col-lg-10">
        <h1>500 - Runtime Error</h1>
        <p class="lead">Hups...Something went wrong <br>
        </p>

        <img src="/s/images/500.jpg" alt="They killed server...">

    <#--#{if play.mode.name() == 'DEV' && exception}
    #{500 exception /}
    #{/if}-->

    </div>
</div>
</body>
</html>