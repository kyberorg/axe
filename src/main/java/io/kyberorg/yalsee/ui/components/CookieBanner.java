package io.kyberorg.yalsee.ui.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Stream;

/**
 * Cookie Banner.
 *
 * @since 3.5
 */
@Slf4j
@Tag("yalsee-cookie-banner")
public class CookieBanner extends Composite<Dialog> {
    public static final String TAG = "[" + CookieBanner.class.getSimpleName() + "]";

    private final VerticalLayout coreLayout = new VerticalLayout();
    private final H3 title = new H3("Cookies Info");

    private final Span textAndLinkSection = new Span();
    private final Span text = new Span("This website uses cookies, because without cookies it works like shit. ");
    private final Anchor link = new Anchor(Endpoint.UI.APP_INFO_PAGE, "More info");

    private final FlexLayout buttons = new FlexLayout();
    private final Button onlyNecessaryButton = new Button("Only necessary cookies");
    private final Button mySelectionButton = new Button("Allow selection");
    private final Button allowAllButton = new Button("Allow all cookies");

    private final FlexLayout checkboxes = new FlexLayout();
    private final Checkbox onlyNecessaryBox = new Checkbox();
    private final Checkbox analyticsBox = new Checkbox();

    public CookieBanner() {
        init();
        applyStyle();
    }

    private void init() {
        onlyNecessaryButton.addClickListener(this::onNecessaryButtonClicked);
        onlyNecessaryButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST);

        mySelectionButton.addClickListener(this::onMySelectionButtonClicked);
        mySelectionButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);

        allowAllButton.addClickListener(this::onAllowAllButtonClicked);
        allowAllButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);

        onlyNecessaryBox.setLabel("Technical");
        onlyNecessaryBox.setValue(true);
        onlyNecessaryBox.setEnabled(false);
        onlyNecessaryBox.setReadOnly(true);

        analyticsBox.setLabel("Analytics");

        textAndLinkSection.add(text, link);
        buttons.add(onlyNecessaryButton, mySelectionButton, allowAllButton);
        checkboxes.add(onlyNecessaryBox, analyticsBox);

        coreLayout.add(title, textAndLinkSection, checkboxes, buttons);

        getContent().add(coreLayout);
    }

    private void applyStyle() {
        setId(Classes.CB_DIALOG);

        buttons.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        Stream<Button> buttonStream = Stream.of(onlyNecessaryButton, mySelectionButton, allowAllButton);
        buttonStream.forEach(button -> button.addClassName(Classes.CB_BUTTON));
    }

    private void onNecessaryButtonClicked(ClickEvent<Button> event) {
        //this is default - so additional actions needed
        writeValuesToSessionAndClose();
    }

    private void onMySelectionButtonClicked(ClickEvent<Button> event) {
        writeValuesToSessionAndClose();
    }

    private void onAllowAllButtonClicked(ClickEvent<Button> event) {
        analyticsBox.setValue(true);
        getContent().close();
    }

    private void writeValuesToSessionAndClose() {
        if (getContent().getUI().isPresent()) {
            final boolean uiHasSession = getContent().getUI().get().getSession() != null;
            if (uiHasSession) {
                final boolean isAnalyticsAllowed = analyticsBox.getValue();
                getContent().getUI().get().getSession()
                        .setAttribute(App.Session.COOKIE_BANNER_ANALYTICS_ALLOWED, isAnalyticsAllowed);
            } else {
                log.warn("{} UI has no session inside. Skipping further actions...", TAG);
            }
        } else {
            log.warn("{} UI is missing. Skipping further actions...", TAG);
        }
        getContent().close();
    }

    public static final class Classes {
        public static final String CB_DIALOG = "cb-dialog";
        public static final String CB_BUTTON = "cb-button";
    }
}
