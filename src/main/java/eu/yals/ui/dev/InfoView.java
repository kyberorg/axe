package eu.yals.ui.dev;

import com.github.appreciated.app.layout.annotations.Caption;
import com.github.appreciated.app.layout.annotations.Icon;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.Endpoint;
import eu.yals.ui.AppView;
import eu.yals.utils.git.GitRepoState;
import eu.yals.utils.maven.MavenInfo;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.INFO_PAGE, layout = AppView.class)
@Caption("Info Page")
@Icon(VaadinIcon.INFO)
@PageTitle("Link shortener for friends: Info View")
public class InfoView extends Div {
    private static final String UNDEFINED = "UNDEFINED";

    public InfoView(GitRepoState gitRepoState, MavenInfo mavenInfo) {
        setId(InfoView.class.getSimpleName());

        String vaadinVersionStr = mavenInfo.hasValues() ? mavenInfo.getVaadinVersion() : UNDEFINED;
        String gitBranchStr = gitRepoState.hasValues() ? gitRepoState.getBranch() : UNDEFINED;
        String gitHostStr = gitRepoState.hasValues() ? gitRepoState.getBuildHost() : UNDEFINED;

        VerticalLayout gitInfo = new VerticalLayout();
        H2 h2 = new H2("Vaadin and Git info");
        Span vaadinVersion = new Span("Vaadin version: " + vaadinVersionStr);
        Span gitBranch = new Span("Git branch: " + gitBranchStr);
        Span gitHost = new Span("Built at " + gitHostStr);

        gitInfo.add(h2, vaadinVersion, gitBranch, gitHost);

        add(gitInfo);
    }
}
