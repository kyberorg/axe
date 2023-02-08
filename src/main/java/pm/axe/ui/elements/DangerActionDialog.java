package pm.axe.ui.elements;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import org.apache.commons.lang3.StringUtils;
import pm.axe.internal.Callback;

/**
 * Confirmation Dialog for actions in {@link pm.axe.ui.pages.user.profile.tabs.DangerZoneTab}.
 *
 */
@CssImport("./css/danger_dialog.css")
public final class DangerActionDialog extends Composite<Dialog> {

    private final H3 title = new H3("Confirm Action");

    private final Span messageSpan = new Span("Proceed below to do this action.");

    private final VerticalLayout warningZone = new VerticalLayout();
    private final Span warningTextSpan = new Span("Are you sure? This action cannot be undone.");

    private final VerticalLayout confirmationZone = new VerticalLayout();
    private final Span confirmationTextSpan = new Span("confirm");
    private final TextField confirmInput = new TextField();

    private final HorizontalLayout buttonLayout = new HorizontalLayout();

    private final Button cancelButton = new Button("Cancel");
    private final Button actionButton = new Button("Proceed");

    private final Dialog dialog = getContent();
    private Registration defaultButtonAction;

    /**
     * Creates dialog. Created dialog ain't open automatically.
     *
     * @return created {@link DangerActionDialog} for further modification.
     */
    public static DangerActionDialog create() {
        return new DangerActionDialog();
    }

    public void setTitleText(final String newTitleText) {
        if (newTitleText.isBlank()) {
            title.setVisible(false);
        }
        title.setText(newTitleText);
    }

    public void setMessage(final String message) {
        if (StringUtils.isBlank(message)) {
            messageSpan.setVisible(false);
        }
        messageSpan.setText(message);
    }

    public void setWarningText(final String warningText) {
        if (StringUtils.isBlank(warningText)) {
            warningZone.setVisible(false);
        }
        warningTextSpan.setText(warningText);
    }

    public void setConfirmationText(final String confirmationText) {
        if (StringUtils.isBlank(confirmationText)) {
            confirmationZone.setVisible(false);
        }
        confirmationTextSpan.setText(confirmationText);
    }

    /**
     * Sets action performed when user pressed Delete Button.
     *
     * @param callback callable method in lambda.
     */
    public void setActionButtonAction(final Callback callback) {
        defaultButtonAction.remove();
        this.actionButton.addClickListener(e -> {
            boolean isConfirmed = checkConfirmationInput();
            if (!isConfirmed) {
                confirmInput.setInvalid(true);
                confirmInput.setErrorMessage("Please type exactly '" + confirmationTextSpan.getText() +"'");
                return;
            }
            callback.execute();
            dialog.close();
        });
    }

    public void setActionButtonText(final String text) {
        if (StringUtils.isNotBlank(text)) {
            actionButton.setText(text);
        }
    }

    /**
     * Displays dialog.
     */
    public void show() {
        this.dialog.open();
    }

    private DangerActionDialog() {
        VerticalLayout dialogLayout = createDialogLayout();
        setClassNames();
        dialog.add(dialogLayout);
    }

    private void setClassNames() {
        dialog.setClassName("danger-dialog");
        title.setClassName("danger-dialog-title");
        messageSpan.setClassName("danger-dialog-message");
        warningZone.setClassName("danger-dialog-warning-zone");
        confirmationTextSpan.setClassName("italic-text");
        buttonLayout.setClassName("danger-dialog-buttons");
        cancelButton.setClassName("danger-dialog-cancel-btn");
        actionButton.setClassName("danger-dialog-action-btn");
    }

    private VerticalLayout createDialogLayout() {
        //warning zone
        Icon warningIcon = VaadinIcon.WARNING.create();
        Text warningTitle = new Text("warning".toUpperCase());
        HorizontalLayout warningLayout = new HorizontalLayout(warningIcon, warningTitle);
        warningLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        warningZone.add(warningLayout, warningTextSpan);

        //confirmation zone
        Span confirmMessageFirst = new Span("Type ");
        Span confirmMessageLast = new Span(" to confirm");

        Span confirmMessage = new Span(confirmMessageFirst, confirmationTextSpan, confirmMessageLast);
        confirmInput.setWidthFull();

        confirmationZone.add(confirmMessage, confirmInput);
        confirmationZone.setPadding(false);

        //by default - no action
        cancelButton.addClickListener(e -> dialog.close());
        defaultButtonAction = actionButton.addClickListener(e -> dialog.close());

        //buttons and button layout
        actionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);

        buttonLayout.add(cancelButton, actionButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        Hr firstLine = new Hr();
        Hr secondLine = new Hr();

        VerticalLayout dialogLayout = new VerticalLayout(title, firstLine,
                messageSpan, warningZone, confirmationZone,
                secondLine, buttonLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.setClassName("delete-dialog");

        return dialogLayout;
    }

    private boolean checkConfirmationInput() {
        if (!confirmationZone.isVisible()) { return true; }

        String inputText = confirmInput.getValue();
        if (StringUtils.isBlank(inputText)) return false;

        String confirmationText = confirmationTextSpan.getText();
        return inputText.trim().equals(confirmationText);
    }
}
