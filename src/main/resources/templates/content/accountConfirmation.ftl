<table width="100%" cellpadding="0" cellspacing="0" style="${appCss}">
    <tr style="${appCss}">
        <td class="content-block" style="${appCss} padding: 0 0 10px;" valign="top">
            <#if username?has_content>
                Hi <b style="${appCss}">${username}</b>.
            </#if>
                Welcome to Axe!
        </td>
    </tr>
    <tr style="${appCss}">
        <td class="content-block" style="${appCss} padding: 0;" valign="top">
            Please confirm your registration by clicking following link: <a href="${link}">${link}</a>. Thank you!
        </td>
    </tr>
    <!-- Empty line -->
    <tr style="${appCss}">
        <td class="content-block" style="${appCss} padding: 0;" valign="top"></td>
    </tr>
    <tr style="${appCss}">
        <td class="content-block last" style="${appCss} padding: 0;" valign="top">
            Did not request any accounts at <a href="${siteUrl}">Axe.pm</a>?
            Ignore this email or <a href="${siteUrl}/register">request</a> one to create nice short links.
        </td>
    </tr>
</table>

<script type="application/ld+json">
        [{
            "@context": "https://schema.org",
            "@type": "EmailMessage",
            "potentialAction": {
                "@type": "ViewAction",
                "target": "${link}",
                "url": "${link}",
                "name": "Confirm your account"
            },
            "description": "Confirm your account by clicking confirmation link",
            "publisher": {
                "@type": "Organization",
                "name": "AxeLinks",
                "url": "https://axe.pm"
            }
        }]
</script>