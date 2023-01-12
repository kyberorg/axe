package pm.axe.ui.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.stream.Stream;

@CssImport(value = "./css/axe_form_styles.css")
public class AxeFormLayout extends AxeBaseLayout {

    @Getter(value = AccessLevel.PROTECTED)
    private final VerticalLayout form = new VerticalLayout();
    private final H2 formTitle = new H2();
    private final Div formSubTitle = new Div();

    private final VerticalLayout fields = new VerticalLayout();
    @Getter(value = AccessLevel.PROTECTED)
    private final Div spaceAfterFields = new Div();
    private final Hr separator = new Hr();

    @Getter(value = AccessLevel.PROTECTED)
    private final Button submitButton = new Button();
    private final Div spaceAfterSubmitButton = new Div();

    /**
     * Initialise Layout elements and styles them.
     */
    public AxeFormLayout() {
        init();
        applyStyle();
    }

    protected void setCompactMode() {
        form.addClassName("axe-compact");
    }

    protected void setFormTitle(final String title) {
        formTitle.setText(title);
        formTitle.setVisible(true);
    }

    protected void setFormSubTitle(final Component... components) {
        formSubTitle.add(components);
        formSubTitle.setVisible(true);
    }

    protected void setFormFields(final Component... components) {
        fields.removeAll();
        fields.add(components);
        fields.setVisible(true);
    }


    protected void setComponentsAfterFields(final Component... components) {
        spaceAfterFields.removeAll();
        spaceAfterFields.add(components);
        spaceAfterFields.setVisible(true);
    }

    protected void setSubmitButtonText(final String submitButtonText) {
        separator.setVisible(true);
        submitButton.setText(submitButtonText);
        submitButton.setVisible(true);
    }

    protected void replaceSubmitButtonWithComponents(final Component... components) {
        Div componentsBox = new Div(components);
        form.replace(submitButton, componentsBox);
    }

    protected void setComponentsAfterSubmitButton(final Component... components) {
        spaceAfterSubmitButton.removeAll();
        spaceAfterSubmitButton.add(components);
        spaceAfterSubmitButton.setVisible(true);
    }

    private void init() {
        // by default all components are hidden
        Stream<Component> formComponents =
                Stream.of(formTitle, formSubTitle, fields, spaceAfterFields,
                        separator, submitButton, spaceAfterSubmitButton);

        formComponents.forEach(component -> {
            component.setVisible(false);
            form.add(component);
        });

        super.add(form);
    }

    private void applyStyle() {
        form.setClassName("axe-form");
        form.addClassName("border");

        formTitle.setClassName("axe-form-title");
        formSubTitle.setClassName("axe-form-subtitle");

        fields.setClassName("axe-fields");
        fields.setSpacing(false);
        fields.addClassName("vertically-compact");

        spaceAfterFields.addClassName("space-after-fields");

        separator.addClassName("separator");

        submitButton.addClassName("submit-button");
        submitButton.setWidthFull();
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        spaceAfterSubmitButton.addClassName("space-after-submit-button");
    }
}
