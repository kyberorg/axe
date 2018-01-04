<#import "lib/head.ftl" as head>
<#import "lib/js.ftl" as js>
<!DOCTYPE html>
<html lang="en">
<head>
    <@head.meta></@head.meta>
    <title>Login to MyYals</title>
    <@head.css></@head.css>
    <@head.loginPage_css></@head.loginPage_css>
    <@head.donkey></@head.donkey>
</head>
<body>
<@js.jquery></@js.jquery>
<@js.jquery_bootstrap></@js.jquery_bootstrap>
<@js.request_js></@js.request_js>
<@js.loginPage_js></@js.loginPage_js>

<!-- Main container -->
<div class="container-fluid">
    <form class="form-signin">
        <h2 class="form-signin-heading">Please sign in</h2>
        <label for="inputEmail" class="sr-only">Email address</label>
        <input type="email" id="inputEmail" class="form-control" placeholder="Email address" required autofocus>
        <label for="inputPassword" class="sr-only">Password</label>
        <input type="password" id="inputPassword" class="form-control" placeholder="Password" required>
        <div class="checkbox">
            <label>
                <input type="checkbox" value="remember-me"> Remember me
            </label>
        </div>
        <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
    </form>
</div><!-- /Main container -->
</body>
</html>