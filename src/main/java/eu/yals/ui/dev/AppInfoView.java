package eu.yals.ui.dev;

import com.github.appreciated.app.layout.annotations.Caption;
import com.github.appreciated.app.layout.annotations.Icon;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Version;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.Endpoint;
import eu.yals.constants.App;
import eu.yals.services.GitService;
import eu.yals.ui.AppView;
import eu.yals.utils.AppUtils;
import eu.yals.utils.git.GitRepoState;
import eu.yals.utils.maven.MavenInfo;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.APP_INFO_PAGE, layout = AppView.class)
@Caption("App Info")
@Icon(VaadinIcon.INFO)
@PageTitle("Link shortener for friends: App Info")
public class AppInfoView extends VerticalLayout {
    private static final String UNDEFINED = "UNDEFINED";

    private final GitService gitService;
    private final GitRepoState gitRepoState;
    private final MavenInfo mavenInfo;
    private final AppUtils appUtils;

    public AppInfoView(GitService gitService, GitRepoState gitRepoState, MavenInfo mavenInfo, AppUtils appUtils) {
        this.gitService = gitService;
        this.gitRepoState = gitRepoState;
        this.mavenInfo = mavenInfo;
        this.appUtils = appUtils;

        init();
    }

    private void init() {
        setId(Id.VIEW_ID);

        HorizontalLayout publicInfoArea = publicInfoArea();
        add(publicInfoArea);

        if (appUtils.isDevelopmentModeActivated() || appUtils.hasDevHeader()) {
            VerticalLayout devInfoArea = devInfoArea();
            add(devInfoArea);
        }
    }

    private HorizontalLayout publicInfoArea() {
        HorizontalLayout publicArea = new HorizontalLayout();
        publicArea.setId(Id.PUBLIC_INFO_AREA);

        String latestTag = gitService.getLatestTag();
        String latestCommit = gitService.getLatestCommit();

        Span versionStart = new Span(String.format("Version %s (based on commit ", latestTag));
        Anchor commit =
                new Anchor(
                        String.format("%s/%s", App.Git.REPOSITORY, latestCommit),
                        latestCommit.substring(0, Integer.min(latestCommit.length(), 7)));
        commit.setId(Id.COMMIT_LINK);
        Span versionEnd = new Span(")");

        Span version = new Span(versionStart, commit, versionEnd);
        version.setId(Id.VERSION);

        publicArea.setWidthFull();

        publicArea.add(version);

        return publicArea;
    }

    private VerticalLayout devInfoArea() {
        VerticalLayout devInfoArea = new VerticalLayout();
        devInfoArea.setId(Id.DEV_INFO_AREA);

        String vaadinVersionStr = mavenInfo.hasValues() ? mavenInfo.getVaadinVersion() : UNDEFINED;
        String vaadinFlowVersion = Version.getFullVersion();

        String gitBranchStr = gitRepoState.hasValues() ? gitRepoState.getBranch() : UNDEFINED;
        String gitHostStr = gitRepoState.hasValues() ? gitRepoState.getBuildHost() : UNDEFINED;

        H3 h3 = new H3("Dev Info");
        Span vaadinVersion = new Span("Vaadin version: " + vaadinVersionStr +
                " (Flow: " + vaadinFlowVersion + ")");

        Span gitBranch = new Span("Git branch: " + gitBranchStr);
        Span gitHost = new Span("Built at " + gitHostStr);

        devInfoArea.add(h3, vaadinVersion, gitBranch, gitHost);

        add(devInfoArea);
        return devInfoArea;
    }

    public static class Id {
        public static final String VIEW_ID = "appInfoView";
        public static final String PUBLIC_INFO_AREA = "publicInfoArea";
        public static final String VERSION = "version";
        public static final String COMMIT_LINK = "commitLink";
        public static final String DEV_INFO_AREA = "devInfoArea";
    }
}
