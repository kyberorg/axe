package io.kyberorg.yalsee;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Inline;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import io.kyberorg.yalsee.session.YalseeSession;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Push
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@PWA(
        name = "Yalsee - the link shortener",
        shortName = "yalsee",
        offlinePath = "offline-page.html",
        offlineResources = {"images/logo.png", "Pebble-Regular.woff"},
        description = "Yalsee - the link shortener")
@Theme(themeClass = Lumo.class, variant = Lumo.LIGHT)
public class WebApplication implements AppShellConfigurator {

    private final AppUtils appUtils;

    @Override
    public void configurePage(final AppShellSettings settings) {
        String title = "Yalsee - the link shortener";
        String description = "The free URL shortener for making long and ugly links into short URLs"
                + " that are easy to share and use.";

        String previewImage = appUtils.getServerUrl() + Endpoint.TNT.PREVIEW_IMAGE;

        settings.addFavIcon("icon", "/icons/favicon-32x32.png", "32x32");
        settings.addLink("shortcut icon", "/icons/favicon-16x16.png");
        settings.addLink("apple-touch-icon", "/icons/apple-touch-icon.png");
        settings.addLink("manifest", "/site.webmanifest");
        settings.addLink("mask-icon", "/icons/safari-pinned-tab.svg");

        settings.addMetaTag("apple-mobile-web-app-title", "yalsee");
        settings.addMetaTag("application-name", "yalsee");
        settings.addMetaTag("msapplication-TileColor", "#ffc40d");
        settings.addMetaTag("theme-color", "#ffffff");

        //SEO Tags
        settings.addMetaTag("title", title);
        settings.addMetaTag("description", description);

        settings.addMetaTag("og:type", "website");
        settings.addMetaTag("og:url", appUtils.getServerUrl());
        settings.addMetaTag("og:title", title);
        settings.addMetaTag("og:description", description);
        settings.addMetaTag("og:image", previewImage);

        settings.addMetaTag("twitter:card", "summary_large_image");
        settings.addMetaTag("twitter:url", appUtils.getServerUrl());
        settings.addMetaTag("twitter:title", title);
        settings.addMetaTag("twitter:description", description);
        settings.addMetaTag("twitter:image", previewImage);

        settings.addInlineFromFile("splash-screen.html", Inline.Wrapping.NONE);
        if (appUtils.isGoogleAnalyticsEnabled() && appUtils.isGoogleAnalyticsAllowed(YalseeSession.getCurrent())) {
            settings.addInlineFromFile(appUtils.getGoggleAnalyticsFileName(), Inline.Wrapping.NONE);
        }
        settings.addInlineFromFile(Inline.Position.PREPEND, "yalsee.js", Inline.Wrapping.JAVASCRIPT);
        settings.addInlineFromFile(Inline.Position.PREPEND, "show-test-name.js", Inline.Wrapping.JAVASCRIPT);
    }
}
