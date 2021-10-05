package io.kyberorg.yalsee.ui.core;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

/**
 * Layout for forms.
 *
 * @since 4.0
 */
@CssImport(value = "./css/yalsee_form_styles.css")
public class YalseeFormLayout extends YalseeLayout {

    public static final String START_POINT = "1px";
    public static final String BREAKPOINT = "646px";

    private final VerticalLayout form = new VerticalLayout();
    private final H2 formTitle = new H2();
    private final Div formSubTitle = new Div();

    private final Div fields = new Div();

    private final Span legalInformationSection = new Span();
    private final Span additionalInformation = new Span();

    private final Hr separator = new Hr();

    private final Button submitButton = new Button();

    /**
     * Initialise Layout elements and styles them.
     */
    public YalseeFormLayout() {
        init();
        applyStyle();
    }

    public void setFormTitle(final String title) {
        formTitle.setText(title);
    }

    public void setFormSubTitle(final Component... components) {
        formSubTitle.add(components);
    }

    public void addFormFields(final Component... components) {
        fields.add(components);
    }

    public void removeFormFields(final Component... components) {
        fields.remove(components);
    }

    public void setLegalInfo(final List<Component> components) {
        components.forEach(legalInformationSection::add);
    }

    public void setAdditionalInfo(final String additionalInfoText) {
        additionalInformation.setText(additionalInfoText);
    }

    public void setSubmitButtonText(final String submitButtonText) {
        submitButton.setText(submitButtonText);
    }

    public void setCompactMode() {
        form.addClassName("yalsee-compact-form");
    }

    private void init() {
        form.add(formTitle, formSubTitle, fields, legalInformationSection, additionalInformation, separator,
                submitButton);
        super.add(form);
    }

    private void applyStyle() {
        form.setClassName(ClassName.FORM);
        form.addClassName("border");

        formTitle.setClassName(ClassName.FORM_TITLE);
        formSubTitle.setClassName(ClassName.FORM_SUBTITLE);

        fields.setClassName(ClassName.FIELDS);

        legalInformationSection.setClassName(ClassName.LEGAL_INFO);
        additionalInformation.setClassName(ClassName.ADDITIONAL_INFO);

        separator.addClassName(ClassName.SEPARATOR);

        submitButton.addClassName(ClassName.SUBMIT_BUTTON);
        submitButton.setWidthFull();
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

    public static class ClassName {
        public static final String FORM = "yalsee-form";
        public static final String FORM_TITLE = "form-title";
        public static final String FORM_SUBTITLE = "yalsee-form-subtitle";
        public static final String FIELDS = "yalsee-fields";
        public static final String LEGAL_INFO = "legal-info";
        public static final String ADDITIONAL_INFO = "additional-info";
        public static final String SEPARATOR = "separator";
        public static final String SUBMIT_BUTTON = "submit-button";
    }
}
