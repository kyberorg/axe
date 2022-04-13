package io.kyberorg.yalsee.ui.pages.appinfo;

import com.vaadin.flow.component.html.Anchor;
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

        final Section generalInfoSection = generalInfoSection();
        final Section cookieSection = cookieSection();
        final Section techInfoSection = techInfoSection();

        removeAll();

        add(generalInfoSection, cookieSection, techInfoSection);
    }

    private Section generalInfoSection() {
        Section genInfoSection = new Section("About Application");
        genInfoSection.setId(IDs.GENERAL_INFO_SECTION);
        genInfoSection.getTitle().setId(IDs.GENERAL_INFO_SECTION_TITLE);

        Span generalInfoSpan = new Span("Yalsee makes your really long links short. "
                + "You can use and share those short links where space really matters.");
        generalInfoSpan.setId(IDs.GENERAL_INFO_SPAN);
        genInfoSection.add(generalInfoSpan);

        if (appUtils.isGoogleAnalyticsEnabled()) {
            Span googleAnalyticsBanner = new Span("This site uses Google Analytics for statistics only. "
                    + "No marketing, advertising or any other bullshit. "
                    + "We respect privacy and DNT (Do Not Track) header.");
            googleAnalyticsBanner.setId(IDs.GOOGLE_ANALYTICS_BANNER);
            genInfoSection.add(googleAnalyticsBanner);
        }

        return genInfoSection;
    }

    private Section cookieSection() {
        Section cookieSection = new Section("About Cookies");
        cookieSection.setId(IDs.COOKIE_SECTION);
        cookieSection.getTitle().setId(IDs.COOKIE_TITLE);

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

        cookieSection.setContent(cookieText, cookieSettingsSpan);
        return cookieSection;
    }

    private Section techInfoSection() {
        Section techInfoSection = new Section("Tech Info");
        techInfoSection.setId(IDs.TECH_INFO_SECTION);

        techInfoSection.getTitle().setId(IDs.TECH_INFO_TITLE);

        techInfoSection.add(versionRaw());

        if (appUtils.isDevelopmentModeActivated() || appUtils.hasDevHeader()) {
            String vaadinVersionStr = mavenInfo.hasValues() ? mavenInfo.getVaadinVersion() : UNDEFINED;
            String vaadinFlowVersion = Version.getFullVersion();

            String gitBranchStr = gitRepoState.hasValues() ? gitRepoState.getBranch() : UNDEFINED;
            String gitHostStr = gitRepoState.hasValues() ? gitRepoState.getBuildHost() : UNDEFINED;

            Span vaadinVersion = new Span("Vaadin version: " + vaadinVersionStr
                    + " (Flow: " + vaadinFlowVersion + ")");

            Span gitBranch = new Span("Git branch: " + gitBranchStr);
            Span gitHost = new Span("Built at " + gitHostStr);

            techInfoSection.add(vaadinVersion);
            techInfoSection.add(gitBranch);
            techInfoSection.add(gitHost);
        }

        add(techInfoSection);
        return techInfoSection;
    }

    private HorizontalLayout versionRaw() {
        HorizontalLayout versionRaw = new HorizontalLayout();

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

        return versionRaw;
    }

    public static class IDs {
        public static final String VIEW_ID = "appInfoView";
        public static final String GENERAL_INFO_SECTION = "genInfoSection";
        public static final String GENERAL_INFO_SECTION_TITLE = "genInfoTitle";
        public static final String GENERAL_INFO_SPAN = "genInfoSpan";
        public static final String GOOGLE_ANALYTICS_BANNER = "googleAnalyticsBanner";
        public static final String COOKIE_SECTION = "cookieSection";
        public static final String COOKIE_TITLE = "cookieTitle";
        public static final String COOKIE_TEXT_SPAN = "cookieTextSpan";
        public static final String COOKIE_LINK = "cookieLink";
        public static final String COOKIE_TECH_DETAILS = "cookieTechDetails";
        public static final String COOKIE_SETTINGS_SPAN = "cookieSettingsSpan";
        public static final String COOKIE_SETTINGS_TEXT = "cookieSettingsText";
        public static final String COOKIE_SETTINGS_LINK = "cookieSettingsLink";
        public static final String COOKIE_SETTINGS_POINT = "cookieSettingsPoint";
        public static final String TECH_INFO_SECTION = "techInfoSection";
        public static final String TECH_INFO_TITLE = "techInfoTitle";
        public static final String VERSION = "version";
        public static final String COMMIT_LINK = "commitLink";
    }
}
