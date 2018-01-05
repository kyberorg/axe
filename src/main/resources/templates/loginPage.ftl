<#import "lib/head.ftl" as head>
<#import "lib/css.ftl" as css>
<#import "lib/js.ftl" as js>
<!DOCTYPE html>
<html lang="en">
<head>
    <@head.meta></@head.meta>
    <title>Login to MyYals</title>
    <@head.css></@head.css>
    <@head.donkey></@head.donkey>
    <@css.loginPage_css></@css.loginPage_css>
</head>
<body>
<@js.jquery></@js.jquery>
<@js.jquery_bootstrap></@js.jquery_bootstrap>
<@js.request_js></@js.request_js>
<@js.loginPage_js></@js.loginPage_js>

<!-- Main container -->
<div class="container-fluid vcenter">
    <form class="login-form">
        <h2 class="login-heading">Please log in</h2>
        <h4 class="login-subheading">to continue to My Yals</h4>
        <label for="username" class="sr-only">Username</label>
        <input type="text" id="username" class="form-control" placeholder="Username" required autofocus>
        <label for="password" class="sr-only">Password</label>
        <input type="password" id="password" class="form-control" placeholder="Password" required>
        <p id="demoString">For demo access use: demo/demo</p>
        <button id="loginButton" class="btn btn-lg btn-primary btn-block" type="submit">Log in</button>
    </form>
</div><!-- /Main container -->
</body>
</html>