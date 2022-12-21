package pm.axe.ui.pages.user;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import pm.axe.Axe;
import pm.axe.Endpoint;
import pm.axe.session.AxeSession;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.AxeBaseLayout;
import pm.axe.ui.pages.home.HomePage;

import java.util.stream.Stream;

/**
 * Page, which shown when confirmation performed by {@link ConfirmationView} succeeded.
 */
@SpringComponent
@UIScope
@Route(value = Endpoint.UI.WELCOME_PAGE, layout = MainView.class)
@PageTitle("Welcome - Axe.pm")
public class WelcomePage extends AxeBaseLayout implements BeforeEnterObserver {
    private final Image logo = new Image();
    private final H1 welcomeTitle = new H1();
    private final Span text = new Span();
    private final Button startButton = new Button();

    /**
     * Creates {@link WelcomePage}.
     */
    public WelcomePage() {
        init();
        applyStyle();
        add(logo, welcomeTitle, text, startButton);
        this.setAlignItems(Alignment.CENTER);
    }

    private void init() {
        logo.setSrc("images/logo_long.png");
        logo.setAlt("Axe Logo");

        welcomeTitle.setText("Welcome to Axe!");
        text.setText("Axe creates short links."); //TODO improve text

        startButton.setText("Get Started");
        startButton.addClickListener(this::onStartButtonClicked);

        Stream<? extends HasStyle> elements = Stream.of(logo, welcomeTitle, text, startButton);
        elements.forEach(e -> e.getStyle().set(Axe.Css.ALIGN_SELF, Axe.CssValues.CENTER));
    }

    private void applyStyle() {
        logo.addClassName("centered-image");
        logo.addClassName("page-wide-image");
        logo.setHeight(logo.getWidth());
        logo.setMaxHeight(Axe.CssValues.EM_15);

        welcomeTitle.getStyle().set(Axe.Css.MARGIN, Axe.CssValues.AUTO);
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        if (event.isRefreshEvent()) return;
        //update title
        AxeSession.getCurrent().ifPresent(axs -> {
            if (axs.hasUser()) {
                welcomeTitle.setText(String.format("Welcome to Axe, %s!", axs.getUser().getUsername()));
            }
        });
        if (isMobileScreen()) {
            logo.setSrc("images/logo.png");
        }
    }

    private void onStartButtonClicked(final ClickEvent<Button> buttonClickEvent) {
        startButton.getUI().ifPresent(ui -> ui.navigate(HomePage.class));
    }

    private boolean isMobileScreen() {
        return AxeSession.getCurrent().map(session -> session.getDevice().isMobile()).orElse(false);
    }
}
