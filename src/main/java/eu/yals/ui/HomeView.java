package eu.yals.ui;

import com.github.appreciated.app.layout.annotations.Caption;
import com.github.appreciated.app.layout.annotations.Icon;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WebBrowser;

@Route(value = "", layout = AppView.class)
@Caption("Home")
@Icon(VaadinIcon.HOME)
public class HomeView extends VerticalLayout {
    private final Board board;
    private final Row firstRow;
    private final Row mainRow;
    private final Row resultRow;
    private final Row qrCodeRow;
    private final HorizontalLayout footer;

    public HomeView() {
        setSizeFull();

        board = new Board();
        firstRow = new Row();
        mainRow = new Row();
        resultRow = new Row();
        qrCodeRow = new Row();
        footer = new HorizontalLayout();

        init();
        css();
    }

    private void init() {
        board.addRow(firstRow);
        board.addRow(mainRow);
        board.addRow(resultRow);
        board.addRow(qrCodeRow);
        add(board);
        WebBrowser browser = VaadinSession.getCurrent().getBrowser();
        if (!browser.isIOS() && !browser.isAndroid()) {
            add(footer);
        }
    }

    private void css() {
        //firstRow.getStyle().set("padding", "15px");
        applyRowStyle(firstRow);

        mainRow.add(emptyDiv(), mainArea(), emptyDiv());
        mainRow.setComponentSpan(mainRow.getComponentAt(1), 2);
        applyRowStyle(mainRow);

        resultRow.add(emptyDiv(), resultArea(), emptyDiv());
        resultRow.setComponentSpan(resultRow.getComponentAt(1), 2);
        applyRowStyle(resultRow);

        qrCodeRow.add(emptyDiv(), qrCodeArea(), emptyDiv());
        qrCodeRow.setComponentSpan(qrCodeRow.getComponentAt(1), 2);
        applyRowStyle(qrCodeRow);

        footer.add(footerArea());
        applyFooterStyle();

        board.setSizeFull();
        footer.setWidthFull();
    }

    private void applyRowStyle(HasStyle component) {
        component.getStyle().set("margin-right", "-15px");
        component.getStyle().set("margin-left", "-15px");
    }

    private void applyBorder(HasStyle component) {
        component.getStyle().set("border", "1px solid #e3e3e3");
        component.getStyle().set("border-radius", "4px");
    }

    private Div emptyDiv() {
        Div div = new Div();
        div.setText("");
        return div;
    }

    private VerticalLayout mainArea() {
        H2 title = new H2("Yet another layout");
        Span subtitle = new Span("... for someone");
        subtitle.getStyle().set("font-style", "italic");

        TextField textField = new TextField("Paste something here:");
        textField.setPlaceholder("link this...");
        textField.setWidthFull();

        Span note = new Span("Note: you are free for all");

        Button button = new Button("Click me!");
        VerticalLayout verticalLayout = new VerticalLayout(title, subtitle, textField, note, button);
        verticalLayout.getStyle().set("background", "#f0f0f0");
        applyBorder(verticalLayout);
        return verticalLayout;
    }

    private HorizontalLayout resultArea() {
        HorizontalLayout resultArea = new HorizontalLayout();
        resultArea.getStyle().set("background", "#dff0d8");
        resultArea.getStyle().set("font-size", "xx-large");
        resultArea.getStyle().set("margin-top", "15px");

        Span emptySpan = new Span();
        Anchor link = new Anchor("https://yals.eu/gMKyrJ", "https://yals.eu/gMKyrJ");
        makeLinkStrong(link);

        Button click = new Button(VaadinIcon.PASTE.create());

        resultArea.add(emptySpan, link, click);
        resultArea.setJustifyContentMode(JustifyContentMode.CENTER);

        applyBorder(resultArea);
        return resultArea;
    }

    private Div qrCodeArea() {
        Div qrCodeArea = new Div();
        Image qrCode = new Image();
        qrCode.setSrc("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAV4AAAFeAQAAAADlUEq3AAABVklEQVR42u3aSw6DIBCAYRIP4JG8ukfyACRTZR4Qa9qumiH5WZiin6spAwMW+b3VAgaDwWAwGJwQ78Wa3Tvsl7UVnB737rFIwyd5eApOi6s9vR6UTdrl6l5/AfBMuIW7jV8wGPxP3LtVZ8mf8jM4DY5lz6551y5f1kjgNLg3nTSPPkw/1SngNFgza4R2L4uMlQh4FtyqjsWzrVaQzykXnAxr85LR58saXXB23CI9ZFsfunrZBJwer73MOOO7xS7OpuMXnB1fze4psZhbLQlOj+/vHubaHsCHbXBwFixRa8SAPU2kYQFnx+1e0Y03j75tCSzvxSM4H76V/7HisTJyBWfH4xacB15PpsCT4OhqEeLZFjwP7sfBdTiFaikXPBUW8QP94kP3fYYFJ8a7nwnrHoAtZsH58ZhyLdyefJ9KD3A2fD8OFhlfW8HZMd8/g8FgMBgMnhC/AP4haj2FcNJGAAAAAElFTkSuQmCC");
        qrCode.setAlt("qrCode");

        qrCodeArea.add(qrCode);
        applyBorder(qrCodeArea);
        qrCodeArea.getStyle().set("text-align", "center");

        return qrCodeArea;
    }

    private Span footerArea() {
        Span versionStart = new Span("Version 2.7 (based on commit ");
        Anchor commit = new Anchor("https://github.com/yadevee/yals/commit/45eba45", "45eba45");
        Span versionEnd = new Span(")");

        Span version = new Span(versionStart, commit, versionEnd);
        version.getStyle().set("color", "#777");

        return version;
    }

    private void makeLinkStrong(HasStyle link) {
        link.getStyle().set("font-weight", "700");
        link.getStyle().set("color", "#337ab7");
        link.getStyle().set("background-color", "transparent");
    }

    private void applyFooterStyle() {
        footer.getStyle().set("position", "absolute");
        footer.getStyle().set("bottom", "0");
        footer.getStyle().set("height", "60px");
        footer.getStyle().set("background-color", "#f5f5f5");

        footer.setJustifyContentMode(JustifyContentMode.CENTER);
        footer.setAlignItems(Alignment.CENTER);
    }

}
