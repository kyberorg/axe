package io.kyberorg.yalsee.ui.core;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import io.kyberorg.yalsee.Endpoint;
import lombok.Getter;

import java.util.List;
import java.util.stream.Stream;

/**
 * Layout for forms.
 *
 * @since 4.0
 */
@CssImport(value = "./css/yalsee_form_styles.css")
public class YalseeFormLayout extends YalseeLayout {

    public static final String START_POINT = "1px";
    public static final String BREAKPOINT = "616px";

    private final VerticalLayout form = new VerticalLayout();
    private final H2 formTitle = new H2();
    private final Div formSubTitle = new Div();

    private final Div fields = new Div();

    private final Span legalInformationSection = new Span();
    private final Span additionalInformation = new Span();

    private final Hr separator = new Hr();

    @Getter
    private final Button submitButton = new Button();

    private final Section forgotPasswordSection = new Section();
    private final Anchor forgotPasswordLink = new Anchor();

    /**
     * Initialise Layout elements and styles them.
     */
    public YalseeFormLayout() {
        init();
        applyStyle();
    }

    public void setCompactMode() {
        form.addClassName("yalsee-compact-form");
    }

    public void setFormTitle(final String title) {
        formTitle.setText(title);
        formTitle.setVisible(true);
    }

    public void setFormSubTitle(final Component... components) {
        formSubTitle.add(components);
        formSubTitle.setVisible(true);
    }

    public void addFormFields(final Component... components) {
        fields.add(components);
        fields.setVisible(true);
    }

    public void removeFormFields(final Component... components) {
        fields.remove(components);
        fields.setVisible(false);
    }

    public void setLegalInfo(final List<Component> components) {
        components.forEach(legalInformationSection::add);
        legalInformationSection.setVisible(true);
    }

    public void setAdditionalInfo(final String additionalInfoText) {
        additionalInformation.setText(additionalInfoText);
        additionalInformation.setVisible(true);
    }

    public void setSubmitButtonText(final String submitButtonText) {
        separator.setVisible(true);
        submitButton.setText(submitButtonText);
        submitButton.setVisible(true);
    }

    public void enableForgotPasswordLink() {
        forgotPasswordSection.setVisible(true);
    }

    public void replaceSubmitButtonWithText(final String text) {
        Span span = new Span(text);
        form.replace(submitButton, span);
    }

    private void init() {
        forgotPasswordLink.setHref(Endpoint.UI.FORGOT_PASSWORD_PAGE);
        forgotPasswordLink.setText("Forgot your password?");
        forgotPasswordSection.add(forgotPasswordLink);

        // by default all components are hidden
        Stream<Component> formComponents =
                Stream.of(formTitle, formSubTitle, fields,
                        legalInformationSection, additionalInformation,
                        separator, submitButton, forgotPasswordSection);

        formComponents.forEach(component -> {
            component.setVisible(false);
            form.add(component);
        });

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

        forgotPasswordLink.setClassName(ClassName.FORGOT_PASSWORD_LINK);
        forgotPasswordSection.setClassName(ClassName.FORGOT_PASSWORD_SECTION);
        forgotPasswordSection.setWidthFull();
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
        public static final String FORGOT_PASSWORD_SECTION = "forgot-password-section";
        public static final String FORGOT_PASSWORD_LINK = "forgot-password-link";
    }
}
