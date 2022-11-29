window.axePiwik = (piwikHost, siteId) => {
    let _paq = window._paq = window._paq || [];
    /* tracker methods like "setCustomDimension" should be called before "trackPageView" */
    _paq.push(['trackPageView']);
    _paq.push(['enableLinkTracking']);
    (function() {
        let u = "https://" + piwikHost + "/";
        _paq.push(['setTrackerUrl', u+'matomo.php']);
        _paq.push(['setSiteId', siteId]);
        let d = document, g = d.createElement('script'), s = d.getElementsByTagName('script')[0];
        g.async=true; g.src=u+'matomo.js'; s.parentNode.insertBefore(g,s);
    })();
}

window.axePiwikOptSwitch = (optOut) => {
    let _paq = window._paq = window._paq || [];
    _paq.push([function () {
        if (optOut) {
            // user is currently opted out
            console.log("Piwik: User opted out")
            _paq.push(['optUserOut']);
        } else {
            // user is currently opted in
            console.log("Piwik: User opted in")
            _paq.push(['forgetUserOptOut']);
        }
    }])
}