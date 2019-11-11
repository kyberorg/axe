package eu.yals.ui;

import com.github.appreciated.app.layout.annotations.Caption;
import com.github.appreciated.app.layout.annotations.Icon;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.button.Button;
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
import eu.yals.services.GitService;
import eu.yals.services.overall.OverallService;
import eu.yals.ui.css.HomeViewCss;
import eu.yals.utils.AppUtils;

@SpringComponent
@UIScope
@Route(value = "", layout = AppView.class)
@Caption("Home")
@Icon(VaadinIcon.HOME)
public class HomeView extends VerticalLayout {

    private final Board board = new Board();
    private final Row firstRow = new Row();
    private final Row mainRow = new Row();
    private final Row resultRow = new Row();
    private final Row qrCodeRow = new Row();

    private final AppUtils appUtils;
    private final HomeViewCss homeViewCss;
    private final OverallService overallService;
    private final GitService gitService;

    public HomeView(AppUtils appUtils, HomeViewCss css, OverallService overallService, GitService gitService) {
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
        resultRow.add(emptyDiv(), resultArea(), emptyDiv());
        qrCodeRow.add(emptyDiv(), qrCodeArea(), emptyDiv());

        board.addRow(firstRow);
        board.addRow(mainRow);
        board.addRow(resultRow);
        board.addRow(qrCodeRow);

        add(board);
        if (appUtils.isMobile(VaadinSession.getCurrent())) {
            add(footer());
        }
    }

    private void applyStyle() {
        mainRow.setComponentSpan(mainRow.getComponentAt(1), 2);
        homeViewCss.applyRowStyle(mainRow);

        resultRow.setComponentSpan(resultRow.getComponentAt(1), 2);
        homeViewCss.applyRowStyle(resultRow);

        qrCodeRow.setComponentSpan(qrCodeRow.getComponentAt(1), 2);
        homeViewCss.applyRowStyle(qrCodeRow);

        board.setSizeFull();
    }

    private void applyLoadState() {

    }

    private Div emptyDiv() {
        Div div = new Div();
        div.setText("");
        return div;
    }

    private VerticalLayout mainArea() {
        H2 title = new H2("Yet another layout");
        Span subtitle = new Span("... for someone");
        homeViewCss.makeSubtitleItalic(subtitle);

        TextField textField = new TextField("Paste something here:");
        textField.setPlaceholder("link this...");
        textField.setWidthFull();

        Span note = new Span("Note: you are free for all");

        Button button = new Button("Click me!");
        VerticalLayout mainArea = new VerticalLayout(title, subtitle, textField, note, button);
        homeViewCss.applyMainAreaStyle(mainArea);
        return mainArea;
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

        Span versionStart = new Span("Version 2.7 (based on commit ");
        Anchor commit = new Anchor("https://github.com/yadevee/yals/commit/45eba45", "45eba45");
        Span versionEnd = new Span(")");

        Span version = new Span(versionStart, commit, versionEnd);
        homeViewCss.paintVersion(version);

        homeViewCss.applyFooterStyle(footer);
        footer.setWidthFull();

        footer.add(version);

        return footer;
    }

}
