package io.kyberorg.yalsee.ui.pages.appinfo;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
import io.kyberorg.yalsee.ui.MainView;
import io.kyberorg.yalsee.ui.elements.Section;
import io.kyberorg.yalsee.ui.layouts.YalseeLayout;
import io.kyberorg.yalsee.utils.AppUtils;
import io.kyberorg.yalsee.utils.git.GitRepoState;
import io.kyberorg.yalsee.utils.maven.MavenInfo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@SpringComponent
@UIScope
@Route(value = Endpoint.UI.APP_INFO_PAGE, layout = MainView.class)
@PageTitle("Yalsee: App Info")
public class AppInfoPage extends YalseeLayout implements BeforeEnterObserver {
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

        final Section publicInfoArea = publicInfoArea();
        final Section cookieArea = cookieArea();

        removeAll();
        add(publicInfoArea, cookieArea);

        if (appUtils.isDevelopmentModeActivated() || appUtils.hasDevHeader()) {
            final Section devInfoArea = devInfoArea();
            add(devInfoArea);
        }
    }

    private Section publicInfoArea() {
        Section publicArea = new Section("About Application");
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

        publicArea.add(versionRaw);

        if (appUtils.isGoogleAnalyticsEnabled()) {
            Span googleAnalyticsBanner = new Span("This site uses Google Analytics for statistics only. "
                    + "We respect privacy and DNT (Do Not Track) header");
            googleAnalyticsBanner.setId(IDs.GOOGLE_ANALYTICS_BANNER);
            publicArea.add(googleAnalyticsBanner);
        }

        return publicArea;
    }

    private Section cookieArea() {
        Section cookieArea = new Section("About Cookies");
        cookieArea.setId(IDs.COOKIE_AREA);

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

        Span cookieSettingsSpan = new Span();
        cookieSettingsSpan.setId(IDs.COOKIE_SETTINGS_SPAN);

        Span cookieSettingsText = new Span("You can find and adjust current cookie settings at ");
        cookieSettingsText.setId(IDs.COOKIE_SETTINGS_TEXT);

        Anchor cookieSettingsLink = new Anchor(Endpoint.UI.SETTINGS_PAGE, "Settings Page");
        cookieSettingsLink.setId(IDs.COOKIE_SETTINGS_LINK);

        Span point = new Span(".");
        point.setId(IDs.COOKIE_SETTINGS_POINT);

        cookieSettingsSpan.add(cookieSettingsText, cookieSettingsLink, point);
        cookieText.add(textStart, link, textEnd, techDetailsText);

        cookieArea.setContent(cookieText, cookieSettingsSpan);
        return cookieArea;
    }

    private Section devInfoArea() {
        Section devInfoArea = new Section();
        devInfoArea.setId(IDs.DEV_INFO_AREA);

        String vaadinVersionStr = mavenInfo.hasValues() ? mavenInfo.getVaadinVersion() : UNDEFINED;
        String vaadinFlowVersion = Version.getFullVersion();

        String gitBranchStr = gitRepoState.hasValues() ? gitRepoState.getBranch() : UNDEFINED;
        String gitHostStr = gitRepoState.hasValues() ? gitRepoState.getBuildHost() : UNDEFINED;


        Span vaadinVersion = new Span("Vaadin version: " + vaadinVersionStr
                + " (Flow: " + vaadinFlowVersion + ")");

        Span gitBranch = new Span("Git branch: " + gitBranchStr);
        Span gitHost = new Span("Built at " + gitHostStr);

        H4 devInfoTitle = new H4("Dev Info");

        devInfoArea.setCustomTitleElement(devInfoTitle);
        devInfoArea.setContent(vaadinVersion, gitBranch, gitHost);

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
        public static final String COOKIE_SETTINGS_SPAN = "cookieSettingsSpan";
        public static final String COOKIE_SETTINGS_TEXT = "cookieSettingsText";
        public static final String COOKIE_SETTINGS_LINK = "cookieSettingsLink";
        public static final String COOKIE_SETTINGS_POINT = "cookieSettingsPoint";

    }
}
