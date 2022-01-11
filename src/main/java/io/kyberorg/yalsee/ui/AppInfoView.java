package io.kyberorg.yalsee.ui;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Version;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.services.GitService;
import io.kyberorg.yalsee.session.YalseeSession;
import io.kyberorg.yalsee.ui.core.YalseeLayout;
import io.kyberorg.yalsee.utils.AppUtils;
import io.kyberorg.yalsee.utils.git.GitRepoState;
import io.kyberorg.yalsee.utils.maven.MavenInfo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@SpringComponent
@UIScope
@Route(value = Endpoint.UI.APP_INFO_PAGE, layout = MainView.class)
@PageTitle("Yalsee: App Info")
public class AppInfoView extends YalseeLayout implements BeforeEnterObserver {
    private static final String UNDEFINED = "UNDEFINED";
    private static final int COMMIT_HASH_LENGTH = 7;

    private final GitService gitService;
    private final GitRepoState gitRepoState;
    private final MavenInfo mavenInfo;
    private final AppUtils appUtils;

    @Override
    public void beforeEnter(final BeforeEnterEvent beforeEnterEvent) {
        init();
    }

    private void init() {
        setId(IDs.VIEW_ID);

        final VerticalLayout publicInfoArea = publicInfoArea();
        final VerticalLayout cookieArea = cookieArea();

        removeAll();
        add(publicInfoArea, cookieArea);

        if (appUtils.isDevelopmentModeActivated() || appUtils.hasDevHeader()) {
            final VerticalLayout devInfoArea = devInfoArea();
            add(devInfoArea);
        }
    }

    private VerticalLayout publicInfoArea() {
        VerticalLayout publicArea = new VerticalLayout();
        HorizontalLayout versionRaw = new HorizontalLayout();
        publicArea.setId(IDs.PUBLIC_INFO_AREA);

        String latestTag = gitService.getLatestTag();
        String latestCommit = gitService.getLatestCommit();

        Span versionStart = new Span(String.format("Version %s (based on commit ", latestTag));
        Anchor commit =
                new Anchor(
                        String.format("%s/%s", App.Git.REPOSITORY, latestCommit),
                        latestCommit.substring(0, Integer.min(latestCommit.length(), COMMIT_HASH_LENGTH)));
        commit.setId(IDs.COMMIT_LINK);
        Span versionEnd = new Span(")");

        Span version = new Span(versionStart, commit, versionEnd);
        version.setId(IDs.VERSION);

        versionRaw.setWidthFull();
        versionRaw.add(version);

        publicArea.setWidthFull();
        publicArea.add(versionRaw);

        if (appUtils.isGoogleAnalyticsEnabled()) {
            Span googleAnalyticsBanner = new Span("This site uses Google Analytics for statistics only. "
                    + "We respect privacy and DNT (Do Not Track) header");
            googleAnalyticsBanner.setId(IDs.GOOGLE_ANALYTICS_BANNER);
            publicArea.add(googleAnalyticsBanner);
        }

        return publicArea;
    }

    private VerticalLayout cookieArea() {
        VerticalLayout cookieArea = new VerticalLayout();
        cookieArea.setId(IDs.COOKIE_AREA);
        H4 title = new H4("About Cookies");
        title.setId(IDs.COOKIE_TITLE);

        Span cookieText = new Span();
        cookieText.setId(IDs.COOKIE_TEXT_SPAN);

        Span textStart = new Span("Yalsee is using ");
        Anchor link = new Anchor("https://www.cookiesandyou.com/", "Cookies");
        link.setId(IDs.COOKIE_LINK);

        Span textEnd = new Span(" to make this site works. ");

        Span techDetailsText = new Span("There are technical cookies like JSESSION, "
                + "what keeps session and preferences "
                + "and analytics cookies (Google Analytics) used for collecting usage statistics.");
        techDetailsText.setId(IDs.COOKIE_TECH_DETAILS);

        H5 cookieCurrentSettingsSubTitle = new H5("Current Settings");
        cookieCurrentSettingsSubTitle.setId(IDs.COOKIE_CURRENT_SETTINGS_TITLE);

        Span techCookies = new Span();
        techCookies.setId(IDs.TECH_COOKIE_SPAN);

        Span techCookiesLabel = new Span("Technical cookies: ");
        techCookiesLabel.setId(IDs.TECH_COOKIE_LABEL);

        ToggleButton techCookiesValue = new ToggleButton(true);
        techCookiesValue.setId(IDs.TECH_COOKIE_VALUE);
        techCookiesValue.setEnabled(false);

        Span analyticsCookies = new Span();
        analyticsCookies.setId(IDs.ANALYTICS_COOKIE_SPAN);

        Span analyticsCookiesLabel = new Span("Analytics cookies: ");
        analyticsCookiesLabel.setId(IDs.ANALYTICS_COOKIE_LABEL);

        ToggleButton analyticsCookiesValue = new ToggleButton();
        analyticsCookiesValue.setId(IDs.ANALYTICS_COOKIE_VALUE);
        analyticsCookiesValue.setValue(appUtils.isGoogleAnalyticsAllowed(YalseeSession.getCurrent()));
        analyticsCookiesValue.addValueChangeListener(event ->
                YalseeSession.getCurrent()
                        .ifPresent(session -> session.getSettings().setAnalyticsCookiesAllowed(event.getValue())));

        cookieText.add(textStart, link, textEnd, techDetailsText);
        techCookies.add(techCookiesLabel, techCookiesValue);
        analyticsCookies.add(analyticsCookiesLabel, analyticsCookiesValue);
        cookieArea.add(title, cookieText, cookieCurrentSettingsSubTitle, techCookies, analyticsCookies);
        return cookieArea;
    }

    private VerticalLayout devInfoArea() {
        VerticalLayout devInfoArea = new VerticalLayout();
        devInfoArea.setId(IDs.DEV_INFO_AREA);

        String vaadinVersionStr = mavenInfo.hasValues() ? mavenInfo.getVaadinVersion() : UNDEFINED;
        String vaadinFlowVersion = Version.getFullVersion();

        String gitBranchStr = gitRepoState.hasValues() ? gitRepoState.getBranch() : UNDEFINED;
        String gitHostStr = gitRepoState.hasValues() ? gitRepoState.getBuildHost() : UNDEFINED;

        H3 h3 = new H3("Dev Info");
        Span vaadinVersion = new Span("Vaadin version: " + vaadinVersionStr
                + " (Flow: " + vaadinFlowVersion + ")");

        Span gitBranch = new Span("Git branch: " + gitBranchStr);
        Span gitHost = new Span("Built at " + gitHostStr);

        devInfoArea.add(h3, vaadinVersion, gitBranch, gitHost);

        add(devInfoArea);
        return devInfoArea;
    }

    public static class IDs {
        public static final String VIEW_ID = "appInfoView";
        public static final String PUBLIC_INFO_AREA = "publicInfoArea";
        public static final String VERSION = "version";
        public static final String COMMIT_LINK = "commitLink";
        public static final String DEV_INFO_AREA = "devInfoArea";
        public static final String GOOGLE_ANALYTICS_BANNER = "googleAnalyticsBanner";
        public static final String COOKIE_AREA = "cookieArea";
        public static final String COOKIE_TITLE = "cookieTitle";
        public static final String COOKIE_TEXT_SPAN = "cookieTextSpan";
        public static final String COOKIE_LINK = "cookieLink";
        public static final String COOKIE_TECH_DETAILS = "cookieTechDetails";
        public static final String COOKIE_CURRENT_SETTINGS_TITLE = "cookieCurrentSettingsTitle";
        public static final String TECH_COOKIE_SPAN = "techCookieSpan";
        public static final String TECH_COOKIE_LABEL = "techCookieLabel";
        public static final String TECH_COOKIE_VALUE = "techCookieValue";
        public static final String ANALYTICS_COOKIE_SPAN = "analyticsCookieSpan";
        public static final String ANALYTICS_COOKIE_LABEL = "analyticsCookieLabel";
        public static final String ANALYTICS_COOKIE_VALUE = "analyticsCookieValue";
    }
}
