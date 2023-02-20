package pm.axe.ui.elements;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.EmailField;
import lombok.Getter;
import pm.axe.mail.EmailConfirmationStatus;

/**
 * {@link EmailField} with {@link EmailConfirmationStatus} in suffix.
 */
public class ConfirmedEmailField extends EmailField {
    @Getter private final EmailConfirmationStatus status;
    @Getter private final Button statusButton;

    /**
     * Creates {@link ConfirmedEmailField}.
     */
    public ConfirmedEmailField() {
        status = EmailConfirmationStatus.NONE;

        statusButton = new Button();
        statusButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
        statusButton.setVisible(false); //no status by default

        this.setSuffixComponent(statusButton);
        this.setTooltipText(status.getStatusString());

        Tooltip tooltip = this.getTooltip().withManual(true);
        statusButton.addClickListener(e -> tooltip.setOpened(!tooltip.isOpened()));
    }

    /**
     * Updates {@link ConfirmedEmailField} with new {@link EmailConfirmationStatus}.
     *
     * @param status new {@link EmailConfirmationStatus}.
     */
    public void setStatus(final EmailConfirmationStatus status) {
        this.setTooltipText(status.getStatusString());
        statusButton.setIcon(status.getIcon().create());
        statusButton.setVisible(status != EmailConfirmationStatus.NONE);
    }
}
