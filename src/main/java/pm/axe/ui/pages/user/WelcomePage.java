package pm.axe.ui.pages.user;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import pm.axe.Endpoint;
import pm.axe.session.AxeSession;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.AxeCompactLayout;
import pm.axe.ui.pages.home.HomePage;
import pm.axe.utils.DeviceUtils;

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
    private final Image logo = new Image();
    private final H2 welcomeTitle = new H2();
    private final Span text = new Span();
    private final Button startButton = new Button();

    private boolean initDone = false;

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        if (event.isRefreshEvent()) return;

        //init
        if (!initDone) {

            init();
            applyStyle();
            add(logo, welcomeTitle, text, startButton);
            initDone = true;
        }

        //update title
        AxeSession.getCurrent().ifPresent(axs -> {
            if (axs.hasUser()) {
                welcomeTitle.setText(String.format("Welcome to Axe, %s!", axs.getUser().getUsername()));
            }
        });
        if (DeviceUtils.isMobileDevice()) {
            logo.setSrc("images/logo.png");
        }
    }

    private void init() {
        logo.setSrc("images/logo_long.png");
        logo.setAlt("Axe Logo");

        welcomeTitle.setText("Welcome to Axe!");
        text.setText("Axe makes your really long links short. " +
                "You can use and share those short links where space really matters.");

        startButton.setText("Get Started");
        startButton.addClickListener(this::onStartButtonClicked);
    }

    private void applyStyle() {
        logo.setClassName("welcome-logo");
        welcomeTitle.setClassName("welcome-title");
        text.setClassName("centered-text");

        Stream<? extends HasStyle> elements = Stream.of(logo, welcomeTitle, text, startButton);
        elements.forEach(e -> e.addClassName("centered-element"));

        this.setAlignItems(Alignment.CENTER);
    }



    private void onStartButtonClicked(final ClickEvent<Button> buttonClickEvent) {
        startButton.getUI().ifPresent(ui -> ui.navigate(HomePage.class));
    }
}
