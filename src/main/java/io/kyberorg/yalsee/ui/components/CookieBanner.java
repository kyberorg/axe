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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import io.kyberorg.yalsee.Endpoint;
import lombok.Getter;

/**
 * Cookie Banner.
 *
 * @since 3.5
 */
@Tag("yalsee-cookie-banner")
public class CookieBanner extends Composite<Dialog> {

    @Getter
    private boolean analyticalCookiesAccepted = false;

    private final VerticalLayout coreLayout = new VerticalLayout();
    private final H3 title = new H3("Cookies Info");

    private final Span textAndLinkSection = new Span();
    private final Span text = new Span("This website uses cookies, because without cookies it works like shit. ");
    private final Anchor link = new Anchor(Endpoint.UI.APP_INFO_PAGE, "More info");

    private final HorizontalLayout buttons = new HorizontalLayout();
    private final Button onlyNecessaryButton = new Button("Only necessary cookies");
    private final Button allowAllButton = new Button("Allow all cookies");

    private final HorizontalLayout checkboxes = new HorizontalLayout();
    private final Checkbox onlyNecessaryBox = new Checkbox();
    private final Checkbox analyticsBox = new Checkbox();

    public CookieBanner() {
        init();
        applyStyle();
    }

    private void init() {
        onlyNecessaryButton.addClickListener(this::onNecessaryButtonClicked);
        onlyNecessaryButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST);

        allowAllButton.addClickListener(this::onAllowAllButtonClicked);
        allowAllButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);

        onlyNecessaryBox.setLabel("Technical");
        onlyNecessaryBox.setValue(true);
        onlyNecessaryBox.setEnabled(false);
        onlyNecessaryBox.setReadOnly(true);

        analyticsBox.setLabel("Analytics");

        textAndLinkSection.add(text, link);
        buttons.add(onlyNecessaryButton, allowAllButton);
        checkboxes.add(onlyNecessaryBox, analyticsBox);

        coreLayout.add(title, textAndLinkSection, buttons, checkboxes);

        getContent().add(coreLayout);
    }

    private void applyStyle() {

    }

    private void onNecessaryButtonClicked(ClickEvent<Button> event) {
        //this is default - so additional actions needed
        getContent().close();
    }

    private void onAllowAllButtonClicked(ClickEvent<Button> event) {
        analyticsBox.setEnabled(true);
        analyticalCookiesAccepted = analyticsBox.getValue();
        getContent().close();
    }
}
