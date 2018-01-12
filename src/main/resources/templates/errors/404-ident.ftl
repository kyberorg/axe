<#import "../lib/head.ftl" as head>
<#import "../lib/css.ftl" as css>
<#import "../lib/js.ftl" as js>
<!DOCTYPE html>
<html lang="en">
<head>
<@head.meta></@head.meta>
    <title>Error 404</title>

<@css.bs></@css.bs>

<@js.donkey></@js.donkey>

</head>
<body>
<div id="top" class="row">&nbsp;</div>
<div class="row">
    <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
        &nbsp;
    </div>
    <div class="col-xs-10 col-sm-10 col-md-10 col-lg-10">
        <h1>404 - No Such Page Exception</h1>
        <p class="lead">We don't have long link that match your short link. <br>
            Make sure you copypasted it fully and without extra characters</p>
        <img src="/s/images/404.jpg" alt="No such page really...">
    </div>
</div>
</body>
</html>