<#import "../lib/head.ftl" as head>
<!DOCTYPE html>
<html lang="en">
<head>
    <@head.meta></@head.meta>
    <title>Error 400</title>
    <@head.css></@head.css>
    <@head.donkey></@head.donkey>
</head>
<body>
<div id="top" class="row">&nbsp;</div>
<div class="row">
    <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
        &nbsp;
    </div>
    <div class="col-xs-10 col-sm-10 col-md-10 col-lg-10">
        <h1>400 - Bad Request</h1>
        <p class="lead">Bad Request <br>
            Server got request, but could not understand it
            <img src="/s/images/400.jpg" alt="Bad request...">
    </div>
</div>
</body>
</html>