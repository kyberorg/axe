package pm.axe.ui.pages.appinfo;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Version;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import pm.axe.Endpoint;
import pm.axe.constants.App;
import pm.axe.services.GitService;
import pm.axe.session.AxeSession;
import pm.axe.ui.MainView;
import pm.axe.ui.elements.Code;
import pm.axe.ui.elements.Section;
import pm.axe.ui.layouts.AxeBaseLayout;
import pm.axe.utils.AppUtils;
import pm.axe.utils.git.GitRepoState;
import pm.axe.utils.maven.MavenInfo;

import static pm.axe.constants.App.ONE_SECOND_IN_MILLIS;

@RequiredArgsConstructor
@SpringComponent
@UIScope
@Route(value = Endpoint.UI.APP_INFO_PAGE, layout = MainView.class)
@PageTitle("Axe.pm: App Info")
public class AppInfoPage extends AxeBaseLayout implements BeforeEnterObserver {
    private static final String UNDEFINED = "UNDEFINED";
    private static final int COMMIT_HASH_LENGTH = 7;
    private final GitService gitService;
    private final GitRepoState gitRepoState;
    private final MavenInfo mavenInfo;
    private final AppUtils appUtils;
    private final MainView mainView;

    private final Notification optOutNotification = makeOptOutNotification();

    @Override
    public void beforeEnter(final BeforeEnterEvent beforeEnterEvent) {
        init();
    }

    private void init() {
        setId(IDs.VIEW_ID);

        final Section generalInfoSection = generalInfoSection();
        final Section cookieSection = cookieSection();
        final Section statsSection = statsSection();
        final Section techInfoSection = techInfoSection();

        boolean isMobile = AxeSession.getCurrent().map(as -> as.getDevice().isMobile()).orElse(false);
        adjustNotificationPosition(isMobile);
        removeAll();

        add(generalInfoSection, cookieSection, statsSection, techInfoSection);
    }

    private Section generalInfoSection() {
        Section genInfoSection = new Section("About Application");
        genInfoSection.setId(IDs.GENERAL_INFO_SECTION);
        genInfoSection.getTitle().setId(IDs.GENERAL_INFO_SECTION_TITLE);

        Span generalInfoSpan = new Span("Axe makes your really long links short. "
                + "You can use and share those short links where space really matters.");
        generalInfoSpan.setId(IDs.GENERAL_INFO_SPAN);
        genInfoSection.add(generalInfoSpan);

        return genInfoSection;
    }

    private Section cookieSection() {
        Section cookieSection = new Section("About Cookies");
        cookieSection.setId(IDs.COOKIE_SECTION);
        cookieSection.getTitle().setId(IDs.COOKIE_TITLE);

        Span cookieText = new Span();
        cookieText.setId(IDs.COOKIE_TEXT_SPAN);

        Span textStart = new Span("Axe is using ");
        Anchor link = new Anchor("https://www.cookiesandyou.com/", "Cookies");
        link.setId(IDs.COOKIE_LINK);

        Span textEnd = new Span(" to make this site works. ");

        Span techDetailsText = new Span("There are technical cookies like JSESSION, "
                + "what keeps session and preferences "
                + "and optional analytics cookies used for collecting usage statistics.");
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

    private Section statsSection() {
        Section usageStatsSection = new Section("About Usage Statistics");

        Span firstTextStart = new Span("Axe collects usage statistics with ethical analytics tool Matomo " +
                "(ex. Piwik). Axe Matomo instance is located at ");
        Code statsAxe = new Code("stats.axe.pm");
        Span firstTextEnd = new Span(" and hosted in Suomi/Finland.");
        Span firstSpan = new Span(firstTextStart, statsAxe, firstTextEnd);


        Span dntInfo = new Span("Matomo respects DNT (Do Not Track) Header.");

        H4 whatCollected = new H4("What is collected?");
        ListItem ip = new ListItem("Visitor IP (if your IP is 1.2.3.4, Matomo will see it as 1.2.0.0)");
        ListItem referer = new ListItem("Referer");
        ListItem geoInfo = new ListItem("Geo Info (Country, Region, City) based on IP address");
        ListItem techData = new ListItem("Tech Info (OS, Browser info, Browser resolution)");
        ListItem actions = new ListItem("Actions performed");

        H4 whyCollected = new H4("Why it is collected?");
        Span whyText = new Span("Usage statistics help Axe developers to understand how people use Axe " +
                "and what devices they use. This information helps to test new features and improvements " +
                "using most popular browsers and resolutions.");

        Span optOutText = new Span("Still want to OptOut? Click ");
        Button optOutButton = new Button("here", e -> {
            mainView.getPiwikStats().optOut(true);
            optOutNotification.open();
        });
        Span optOutSpan = new Span(optOutText, optOutButton);

        usageStatsSection.setContent(firstSpan, dntInfo, whatCollected, ip, referer, geoInfo, techData, actions,
                whyCollected, whyText, optOutSpan);

        return usageStatsSection;
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

    private Notification makeOptOutNotification() {
        Notification notification = new Notification("Done");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.setDuration(ONE_SECOND_IN_MILLIS); //1 second
        return notification;
    }

    private void adjustNotificationPosition(final boolean isMobile) {
        Notification.Position position = isMobile ? Notification.Position.BOTTOM_CENTER : Notification.Position.MIDDLE;
        this.optOutNotification.setPosition(position);
    }

    public static class IDs {
        public static final String VIEW_ID = "appInfoView";
        public static final String GENERAL_INFO_SECTION = "genInfoSection";
        public static final String GENERAL_INFO_SECTION_TITLE = "genInfoTitle";
        public static final String GENERAL_INFO_SPAN = "genInfoSpan";
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
