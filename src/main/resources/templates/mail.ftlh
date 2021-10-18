<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns="http://www.w3.org/1999/xhtml">
<#assign appStyle="margin: 0; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 16px; color: #333; line-height: 25px; -webkit-font-smoothing: antialiased; -webkit-text-size-adjust: none;">
<#include "parts/head.ftl" parse=true>

<body style="-webkit-font-smoothing: antialiased; -webkit-text-size-adjust: none; height: 100%; line-height: 25px; width: 100% !important; margin: 0;"
      bgcolor="#f0f0f0">
<#include "parts/styles.ftl" parse=false>

<table width="100%" cellpadding="0" cellspacing="0" bgcolor="#f0f0f0">
    <tr>
        <td class="container" width="100%" align="center">
            <table cellpadding="0" cellspacing="0" width="100%" class="content-table">
                <tr>
                    <td></td>
                    <td class="content" align="center" valign="top" width="600" style="padding-bottom: 20px;">
                        <#include "parts/image-table.ftl" parse=false>
                        <table class="main" cellpadding="0" cellspacing="0"
                               style="border: 1px solid #e9e9e9; border-radius: 3px;" bgcolor="white">
                            <tr>
                                <td class="content-wrap"
                                    style="font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 16px; color: #333; line-height: 25px; margin: 0; -webkit-font-smoothing: antialiased; padding: 20px; -webkit-text-size-adjust: none;"
                                    valign="top">

                                    <table width="100%" cellpadding="0" cellspacing="0"
                                           style="${appStyle}">
                                        <tr style="${appStyle}">
                                            <td class="content-block"
                                                style="font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 16px; color: #333; line-height: 25px; margin: 0; -webkit-font-smoothing: antialiased; padding: 0 0 10px; -webkit-text-size-adjust: none;"
                                                valign="top">
                                                Your two-step verification code is: <b
                                                        style="${appStyle}">${code}</b>
                                            </td>
                                        </tr>
                                        <tr style="${appStyle}">
                                            <td class="content-block last"
                                                style="font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 16px; color: #333; line-height: 25px; margin: 0; -webkit-font-smoothing: antialiased; padding: 0; -webkit-text-size-adjust: none;"
                                                valign="top">
                                                Use this code to complete logging in with Yalsee.
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                    </td>
                    <td></td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</body>
</html>