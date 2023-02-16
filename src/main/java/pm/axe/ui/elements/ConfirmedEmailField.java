package pm.axe.ui.elements;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.EmailField;
import lombok.Getter;
import pm.axe.mail.EmailConfirmationStatus;

public class ConfirmedEmailField extends Composite<EmailField> {
    @Getter private final EmailConfirmationStatus status;

    @Getter private final Button statusButton;

    public ConfirmedEmailField() {
        status = EmailConfirmationStatus.NONE;

        statusButton = new Button();
        statusButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
        statusButton.setVisible(false); //no status by default

        getContent().setSuffixComponent(statusButton);
        getContent().setTooltipText(status.getStatusString());

        Tooltip tooltip = getContent().getTooltip().withManual(true);
        statusButton.addClickListener(e -> tooltip.setOpened(!tooltip.isOpened()));
    }
    
    public void setStatus(final EmailConfirmationStatus status) {
        getContent().setTooltipText(status.getStatusString());
        statusButton.setIcon(status.getIcon().create());
        statusButton.setVisible(status != EmailConfirmationStatus.NONE);
    }

    public EmailField get() {
        return getContent();
    }

}
