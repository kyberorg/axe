package pm.axe.ui.elements;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import org.apache.commons.lang3.RandomStringUtils;
import pm.axe.utils.AppUtils;
import pm.axe.utils.ClipboardUtils;


@SuppressWarnings({"FieldCanBeLocal"})
public class PasswordGenerator extends Composite<Details> {
    private static final String SUMMARY_TEXT = "Password Generator";
    private static final int DEFAULT_LEN = 24;
    private final Code generatedPasswordHolder = new Code();
    private final Button useItButton = new Button(VaadinIcon.COPY.create());
    private final Button redoButton = new Button(VaadinIcon.REFRESH.create());

    private PasswordField target;

    public static PasswordGenerator create() {
        return new PasswordGenerator();
    }

    public void setCopyTarget(final PasswordField component) {
        if (component == null) throw new IllegalArgumentException("cannot copy to null component");
        this.target = component;
    }

    public void setOpened(final boolean shouldOpen) {
        getContent().setOpened(shouldOpen);
    }

    private PasswordGenerator() {
        getContent().setSummaryText(SUMMARY_TEXT);

        generatedPasswordHolder.setText(generatePassword());

        useItButton.setTooltipText("Use this password");
        useItButton.addClickListener(this::onUseThisPasswordClicked);
        useItButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        ClipboardUtils.setCopyToClipboardFunctionFor(useItButton);
        ClipboardUtils.setTextToCopy(generatedPasswordHolder.getText()).forComponent(useItButton);

        redoButton.setTooltipText("Re-generate it");
        redoButton.addClickListener(this::onRedoButtonClicked);
        redoButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        FlexLayout content = new FlexLayout();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setJustifyContentMode(FlexComponent.JustifyContentMode.AROUND);
        content.add(generatedPasswordHolder, useItButton, redoButton);
        content.expand(generatedPasswordHolder);
        getContent().setContent(content);
    }

    private void onUseThisPasswordClicked(ClickEvent<Button> event) {
        if (target != null) {
            //copy to component
            target.setValue(generatedPasswordHolder.getText());
            target.getElement().callJsFunction("_setPasswordVisible", true);
            //suggestion accepted - we can close Password Generator
            getContent().setOpened(false);
            //show info notification as well
            AppUtils.showInfoNotification("Also copied to clipboard, so you can record it.");
        } else {
            //copy to clipboard
            ClipboardUtils.showLinkCopiedNotification("Password copied", Notification.Position.MIDDLE);
        }
    }

    private void onRedoButtonClicked(ClickEvent<Button> event) {
        generatedPasswordHolder.setText(generatePassword());
        ClipboardUtils.setTextToCopy(generatedPasswordHolder.getText()).forComponent(useItButton);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(DEFAULT_LEN);
    }
}
