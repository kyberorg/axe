package pm.axe.ui.pages.user;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import pm.axe.Axe;
import pm.axe.Endpoint;
import pm.axe.db.models.Token;
import pm.axe.db.models.User;
import pm.axe.services.user.TokenService;
import pm.axe.session.AxeSession;
import pm.axe.telegram.TelegramCommand;
import pm.axe.ui.MainView;
import pm.axe.ui.elements.Code;
import pm.axe.ui.elements.CopyToClipboardIcon;
import pm.axe.ui.elements.Section;
import pm.axe.ui.layouts.AxeCompactLayout;
import pm.axe.ui.pages.home.HomePage;
import pm.axe.utils.AppUtils;
import pm.axe.utils.ClipboardUtils;
import pm.axe.utils.DeviceUtils;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Page, which shown when confirmation performed by {@link ConfirmationView} succeeded.
 */
@RequiredArgsConstructor
@SpringComponent
@UIScope
@Route(value = Endpoint.UI.WELCOME_PAGE, layout = MainView.class)
@PageTitle("Welcome - Axe.pm")
@CssImport(value = "./css/welcome_page.css")
public class WelcomePage extends AxeCompactLayout implements BeforeEnterObserver {
    private final TokenService tokenService;
    private final Image logo = new Image();
    private final H2 welcomeTitle = new H2();
    private final Span text = new Span();
    private final Button startButton = new Button();

    private final Accordion telegramAccordion = new Accordion();
    private final Section telegramSection = new Section();

    private boolean initDone = false;

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        if (event.isRefreshEvent()) return;

        //init
        if (!initDone) {

            init();
            applyStyle();
            add(logo, welcomeTitle, text, telegramAccordion, startButton);
            initDone = true;
        }

        //Update title and telegram accordion state.
        AxeSession.getCurrent().ifPresent(axs -> {
            if (axs.hasUser()) {
                welcomeTitle.setText(String.format("Welcome to Axe, %s!", axs.getUser().getUsername()));
                telegramAccordion.setVisible(StringUtils.isNotBlank(getTelegramToken()));
            }
        });

        //Update Image
        if (DeviceUtils.isMobileDevice()) {
            logo.setSrc("images/logo.png");
        }
    }

    private void init() {
        logo.setSrc("images/logo_long.png");
        logo.setAlt("Axe Logo");

        welcomeTitle.setText("Welcome to Axe!");
        text.setText("Axe makes your really long links short. "
                + "You can use and share those short links where space really matters.");

        telegramAccordion.add("Have Telegram?", getTelegramInfo());
        telegramAccordion.close();
        telegramAccordion.setVisible(false);

        telegramSection.addClassName("telegram-section");

        startButton.setText("Get Started");
        startButton.addClickListener(this::onStartButtonClicked);
    }

    private void applyStyle() {
        logo.setClassName("welcome-logo");
        welcomeTitle.setClassName("welcome-title");
        welcomeTitle.addClassName("centered-text");
        text.setClassName("centered-text");

        Stream<? extends HasStyle> elements = Stream.of(logo, welcomeTitle, text, telegramAccordion, startButton);
        elements.forEach(e -> e.addClassName("centered-element"));

        this.setAlignItems(Alignment.CENTER);
    }

    private Section getTelegramInfo() {
        String tgCommand = TelegramCommand.HELLO.getCommandText();
        String tgToken = getTelegramToken();

        Span telegramSpan = new Span();

        Span startSpan = new Span("Send ");
        Code tgString = new Code(String.format("%s %s", tgCommand, tgToken));
        CopyToClipboardIcon copyCommandIcon = new CopyToClipboardIcon();
        Span toSpan = new Span(" to ");

        String botName = AppUtils.getTelegramBotName();
        String telegramLink = String.format("%s%s", Axe.Telegram.TELEGRAM_URL, botName);
        Anchor botLink = new Anchor(telegramLink, "@" + botName);

        Span endSpan = new Span(" to link your account with Telegram.");

        telegramSpan.add(startSpan, tgString, copyCommandIcon, toSpan, botLink, endSpan);
        telegramSection.add(telegramSpan);

        copyCommandIcon.setClassName("copy-command-icon");
        copyCommandIcon.getContent().addClickListener(e ->  {
            copyCommandIcon.setTextToCopy(tgString.getText());
            Notification.Position position = DeviceUtils.isMobileDevice()
                    ? Notification.Position.BOTTOM_CENTER : Notification.Position.MIDDLE;
            ClipboardUtils.getLinkCopiedNotification("Copied!", position).open();
        });

        return telegramSection;
    }

    private void onStartButtonClicked(final ClickEvent<Button> buttonClickEvent) {
        startButton.getUI().ifPresent(ui -> ui.navigate(HomePage.class));
    }

    private String getTelegramToken() {
        boolean hasUser = AxeSession.getCurrent().map(AxeSession::hasUser).orElse(false);
        if (hasUser) {
            User user = AxeSession.getCurrent().get().getUser();
            Optional<Token> telegramToken = tokenService.getTelegramToken(user);
            return telegramToken.map(Token::getToken).orElse("");
        } else {
            return "";
        }
    }
}
