package pm.axe.ui.elements;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import org.apache.commons.lang3.StringUtils;
import pm.axe.internal.Callback;

/**
 * "Are you sure?" Dialog.
 *
 */
public final class AreYouSureDialog extends Composite<Dialog> {

    private final H3 title = new H3();
    private final Span message = new Span("Are you sure?");
    private final Button cancelButton = new Button("Cancel");
    private final Button actionButton = new Button("Action");

    private final Dialog dialog = getContent();
    private Registration defaultActionButtonAction;

    /**
     * Creates dialog. Created dialog ain't open automatically.
     *
     * @param titleText string with title text
     * @return created {@link AreYouSureDialog} for further modification.
     */
    public static AreYouSureDialog create(final String titleText) {
        return new AreYouSureDialog(titleText);
    }

    /**
     * Sets action performed when user pressed Delete Button.
     *
     * @param callback callable method in lambda.
     */
    public void setActionButtonAction(final Callback callback) {
        defaultActionButtonAction.remove();
        this.actionButton.addClickListener(e -> {
            callback.execute();
            dialog.close();
        });
    }

    /**
     * Displays dialog.
     */
    public void show() {
        this.dialog.open();
    }

    public void setActionButtonText(final String text) {
        if (StringUtils.isNotBlank(text)) {
            actionButton.setText(text);
        }
    }

    private AreYouSureDialog(final String titleTextString) {
        VerticalLayout dialogLayout = createDialogLayout();
        setClassNames();
        dialog.add(dialogLayout);

        final String titleText = StringUtils.isNotBlank(titleTextString) ? titleTextString : "Title";
        title.setText(titleText);
    }

    private void setClassNames() {
        title.setClassName("sure-dialog-title");
        message.setClassName("sure-dialog-message");
        cancelButton.setClassName("sure-dialog-cancel-btn");
        actionButton.setClassName("sure-dialog-delete-btn");
    }

    private VerticalLayout createDialogLayout() {
        //by default - no action
        cancelButton.addClickListener(e -> dialog.close());
        defaultActionButtonAction = actionButton.addClickListener(e -> dialog.close());

        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        actionButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, actionButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout dialogLayout = new VerticalLayout(title, message, buttonLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.setClassName("sure-dialog");

        return dialogLayout;
    }
}
