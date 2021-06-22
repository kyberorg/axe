package io.kyberorg.yalsee.ui.dev;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Version;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.services.GitService;
import io.kyberorg.yalsee.ui.MainView;
import io.kyberorg.yalsee.ui.core.YalseeLayout;
import io.kyberorg.yalsee.utils.AppUtils;
import io.kyberorg.yalsee.utils.git.GitRepoState;
import io.kyberorg.yalsee.utils.maven.MavenInfo;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.APP_INFO_PAGE, layout = MainView.class)
@PageTitle("Yalsee: App Info")
public class AppInfoView extends YalseeLayout {
    private static final String UNDEFINED = "UNDEFINED";
    private static final int COMMIT_HASH_LENGTH = 7;

    private final GitService gitService;
    private final GitRepoState gitRepoState;
    private final MavenInfo mavenInfo;
    private final AppUtils appUtils;

    /**
     * Creates {@link AppInfoView} object.
     *
     * @param gitService   information from git
     * @param gitRepoState information from build time
     * @param mavenInfo    info from maven
     * @param appUtils     application utils
     */
    public AppInfoView(final GitService gitService, final GitRepoState gitRepoState,
                       final MavenInfo mavenInfo, final AppUtils appUtils) {
        this.gitService = gitService;
        this.gitRepoState = gitRepoState;
        this.mavenInfo = mavenInfo;
        this.appUtils = appUtils;

        init();
    }

    private void init() {
        setId(IDs.VIEW_ID);

        VerticalLayout publicInfoArea = publicInfoArea();
        add(publicInfoArea);

        if (appUtils.isDevelopmentModeActivated() || appUtils.hasDevHeader()) {
            VerticalLayout devInfoArea = devInfoArea();
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
    }
}
