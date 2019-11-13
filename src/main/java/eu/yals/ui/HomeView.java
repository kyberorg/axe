package eu.yals.ui;

import com.github.appreciated.app.layout.annotations.Caption;
import com.github.appreciated.app.layout.annotations.Icon;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.constants.App;
import eu.yals.services.GitService;
import eu.yals.services.overall.OverallService;
import eu.yals.ui.css.HomeViewCss;
import eu.yals.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;

@Slf4j
@SpringComponent
@UIScope
@Route(value = "", layout = AppView.class)
@Caption("Home")
@Icon(VaadinIcon.HOME)
public class HomeView extends VerticalLayout {
    private static final String TAG = "[Front Page]";

    private final Board board = new Board();
    private final Row firstRow = new Row();
    private final Row mainRow = new Row();
    private final Row overallRow = new Row();
    private final Row resultRow = new Row();
    private final Row qrCodeRow = new Row();

    private final AppUtils appUtils;
    private final HomeViewCss homeViewCss;
    private final OverallService overallService;
    private final GitService gitService;

    private String latestCommit;
    private String latestTag;

    private Span linkCounter;

    public HomeView(AppUtils appUtils, HomeViewCss css, @Qualifier("dbOverallService") OverallService overallService, GitService gitService) {
        this.appUtils = appUtils;
        this.homeViewCss = css;
        this.overallService = overallService;
        this.gitService = gitService;

        setId("root");
        init();
        applyStyle();
        applyLoadState();
    }

    private void init() {
        mainRow.add(emptyDiv(), mainArea(), emptyDiv());
        overallRow.add(emptyDiv(), overallArea(), emptyDiv());
        resultRow.add(emptyDiv(), resultArea(), emptyDiv());
        qrCodeRow.add(emptyDiv(), qrCodeArea(), emptyDiv());

        board.addRow(firstRow);
        board.addRow(mainRow);
        board.addRow(overallRow);
        board.addRow(resultRow);
        board.addRow(qrCodeRow);

        add(board);
        if (appUtils.isNotMobile(VaadinSession.getCurrent())) {
            prepareGitInfoForFooter();
            if (displayFooter()) {
                add(footer());
            }
        }
    }

    private void applyStyle() {
        mainRow.setComponentSpan(mainRow.getComponentAt(1), 2);
        homeViewCss.applyRowStyle(mainRow);

        overallRow.setComponentSpan(overallRow.getComponentAt(1), 2);
        homeViewCss.applyRowStyle(overallRow);

        resultRow.setComponentSpan(resultRow.getComponentAt(1), 2);
        homeViewCss.applyRowStyle(resultRow);

        qrCodeRow.setComponentSpan(qrCodeRow.getComponentAt(1), 2);
        homeViewCss.applyRowStyle(qrCodeRow);

        board.setSizeFull();
    }

    private void applyLoadState() {
        long linksStored = overallService.numberOfStoredLinks();
        linkCounter.setText(Long.toString(linksStored));

        mainRow.setVisible(true);
        overallRow.setVisible(true);
        resultRow.setVisible(false);
        qrCodeRow.setVisible(false);
    }

    private void prepareGitInfoForFooter() {
        latestCommit = gitService.getGitInfoSource().getLatestCommitHash().trim();
        latestTag = gitService.getGitInfoSource().getLatestTag().trim();
    }

    private boolean displayFooter() {
        boolean commitPresent = (!latestCommit.equals(App.NO_VALUE) && StringUtils.isNotBlank(latestCommit));
        boolean tagPresent = (!latestTag.equals(App.NO_VALUE) && StringUtils.isNotBlank(latestTag));

        boolean displayCommitInfo = commitPresent && tagPresent;
        log.trace("{} will I display footer: {}. Commit present: {}. Tag present: {} ",
                TAG, displayCommitInfo, commitPresent, tagPresent);

        return displayCommitInfo;
    }

    private Div emptyDiv() {
        Div div = new Div();
        div.setText("");
        return div;
    }

    private VerticalLayout mainArea() {
        H2 title = new H2("Yet another link shortener");
        Span subtitle = new Span("... for friends");
        homeViewCss.makeSubtitleItalic(subtitle);

        TextField textField = new TextField("Your very long URL here:");
        textField.setPlaceholder("http://mysuperlongurlhere.tld");
        textField.setWidthFull();

        Span note = new Span("Note: all links considered as public and can be used by anyone");

        Button button = new Button("Shorten it!");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        VerticalLayout mainArea = new VerticalLayout(title, subtitle, textField, note, button);
        mainArea.setId("mainArea");
        homeViewCss.applyMainAreaStyle(mainArea);
        return mainArea;
    }

    private HorizontalLayout overallArea() {
        Span overallText = new Span("Yals already saved ");
        linkCounter = new Span();
        Span links = new Span(" links");

        HorizontalLayout overallArea = new HorizontalLayout(overallText, linkCounter, links);
        homeViewCss.applyOverallAreaStyle(overallArea);
        return overallArea;
    }

    private HorizontalLayout resultArea() {
        HorizontalLayout resultArea = new HorizontalLayout();

        Span emptySpan = new Span();

        Anchor link = new Anchor("https://yals.eu/gMKyrJ", "https://yals.eu/gMKyrJ");
        homeViewCss.makeLinkStrong(link);

        Button click = new Button(VaadinIcon.PASTE.create());

        homeViewCss.applyResultAreaStyle(resultArea);
        resultArea.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        resultArea.add(emptySpan, link, click);
        return resultArea;
    }

    private Div qrCodeArea() {
        Div qrCodeArea = new Div();
        Image qrCode = new Image();
        qrCode.setSrc("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAV4AAAFeAQAAAADlUEq3AAABVklEQVR42u3aSw6DIBCAYRIP4JG8ukfyACRTZR4Qa9qumiH5WZiin6spAwMW+b3VAgaDwWAwGJwQ78Wa3Tvsl7UVnB737rFIwyd5eApOi6s9vR6UTdrl6l5/AfBMuIW7jV8wGPxP3LtVZ8mf8jM4DY5lz6551y5f1kjgNLg3nTSPPkw/1SngNFgza4R2L4uMlQh4FtyqjsWzrVaQzykXnAxr85LR58saXXB23CI9ZFsfunrZBJwer73MOOO7xS7OpuMXnB1fze4psZhbLQlOj+/vHubaHsCHbXBwFixRa8SAPU2kYQFnx+1e0Y03j75tCSzvxSM4H76V/7HisTJyBWfH4xacB15PpsCT4OhqEeLZFjwP7sfBdTiFaikXPBUW8QP94kP3fYYFJ8a7nwnrHoAtZsH58ZhyLdyefJ9KD3A2fD8OFhlfW8HZMd8/g8FgMBgMnhC/AP4haj2FcNJGAAAAAElFTkSuQmCC");
        qrCode.setAlt("qrCode");

        homeViewCss.applyQrCodeAreaStyle(qrCodeArea);

        qrCodeArea.add(qrCode);
        return qrCodeArea;
    }

    private HorizontalLayout footer() {
        HorizontalLayout footer = new HorizontalLayout();

        Span versionStart = new Span(String.format("Version %s (based on commit ", this.latestTag));
        Anchor commit = new Anchor(String.format("%s/%s", App.Git.REPOSITORY, this.latestCommit),
                latestCommit.substring(0, Integer.min(latestCommit.length(), 7)));
        Span versionEnd = new Span(")");

        Span version = new Span(versionStart, commit, versionEnd);
        homeViewCss.paintVersion(version);

        homeViewCss.applyFooterStyle(footer);
        footer.setWidthFull();

        footer.add(version);

        return footer;
    }

}
