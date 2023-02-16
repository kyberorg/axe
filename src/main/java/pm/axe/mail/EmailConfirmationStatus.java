package pm.axe.mail;

import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.Getter;

public enum EmailConfirmationStatus {
    CONFIRMED(VaadinIcon.CHECK, "Email confirmed"),
    PENDING(VaadinIcon.ELLIPSIS_CIRCLE, "Validation pending..."),
    FAILED(VaadinIcon.FIRE, "Validation failed"),
    NONE(VaadinIcon.COG, "No status defined yet");

    @Getter
    private final VaadinIcon icon;
    @Getter private final String statusString;
    EmailConfirmationStatus(final VaadinIcon icon, final String statusString) {
        this.icon = icon;
        this.statusString = statusString;
    }
}
