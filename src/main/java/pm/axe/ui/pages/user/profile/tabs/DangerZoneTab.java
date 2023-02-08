package pm.axe.ui.pages.user.profile.tabs;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import pm.axe.db.models.User;
import pm.axe.internal.HasTabInit;
import pm.axe.ui.elements.DangerActionDialog;
import pm.axe.ui.elements.Section;
import pm.axe.utils.AppUtils;
import pm.axe.utils.VaadinUtils;

import java.util.stream.Stream;


@RequiredArgsConstructor
@SpringComponent
@UIScope
public class DangerZoneTab extends VerticalLayout implements HasTabInit {
    private Section deleteAccountSection;
    private User user;
    @Override
    public void tabInit(final User user) {
        this.user = user;
        createDeleteAccountSection();
        add(deleteAccountSection);
    }

    private void createDeleteAccountSection() {
        deleteAccountSection = new Section("Delete my Account");
        deleteAccountSection.setCentered();
        Component content = deleteAccountSectionContent();
        deleteAccountSection.setContent(content);
    }

    private Component deleteAccountSectionContent() {
        deleteAccountSection.getTitle().addClassName("bold");

        Button deAuthSessionsButton = new Button("Deauthorize Sessions");
        deAuthSessionsButton.addClickListener(this::onDeAuthSessions);

        Button deleteAccountOnlyButton = new Button("Delete Account");
        deleteAccountOnlyButton.addClickListener(this::onDeleteAccount);

        Button deleteAccountAndLinksButton = new Button("Delete Account and Links");
        deleteAccountAndLinksButton.addClickListener(this::onDeleteAccountAndLinks);

        Stream.of(deAuthSessionsButton, deleteAccountOnlyButton, deleteAccountAndLinksButton)
                .forEach(b -> b.addThemeVariants(ButtonVariant.LUMO_ERROR));

        HorizontalLayout layout = new HorizontalLayout(deAuthSessionsButton,
                deleteAccountOnlyButton, deleteAccountAndLinksButton);
        VaadinUtils.fitLayoutInWindow(layout);
        return layout;
    }

    private void onDeAuthSessions(final ClickEvent<Button> event) {
        if (user == null) return; //no user - no action
        DangerActionDialog dialog = deAuthSessionDialog();
        dialog.show();
    }

    private void onDeleteAccount(final ClickEvent<Button> event) {
        if (user == null) return; //no user - no action
        DangerActionDialog dialog = deleteAccountDialog();
        dialog.show();
    }

    private void onDeleteAccountAndLinks(final ClickEvent<Button> event) {
        if (user == null) return; //no user - no action
        DangerActionDialog dialog = deleteAccountAndLinksDialog();
        dialog.show();
    }

    private DangerActionDialog deAuthSessionDialog() {
        DangerActionDialog dialog = DangerActionDialog.create();
        dialog.setTitleText("Deauthorise sessions");
        dialog.setMessage("Concerned your account is logged in on another device? "
                + "Proceed below to deauthorise all computers or devices that you have previously used. "
                + "This security step is recommended if you previously used a public computer "
                + "or accidentally saved your password on a device that isn't yours.");

        dialog.setWarningText("Proceeding will also log you out of your current session, requiring you to log back in");
        dialog.setConfirmationText("confirm");
        dialog.setActionButtonText("Deauthorise sessions");
        dialog.setActionButtonAction(this::deAuthSessions);
        return dialog;
    }

    private DangerActionDialog deleteAccountDialog() {
        DangerActionDialog dialog = DangerActionDialog.create();
        dialog.setTitleText("Delete Account");
        dialog.setMessage("This will delete all items (linked accounts, settings and so on) "
                + "connected with given account. This will preserve links created, so you can continue to use them, "
                + "but you cannot change or delete them.");
        dialog.setWarningText("Deleting Account is permanent. It cannot be undone.");
        dialog.setConfirmationText("delete");
        dialog.setActionButtonText("Delete Account");
        dialog.setActionButtonAction(this::deleteAccount);
        return dialog;
    }

    private DangerActionDialog deleteAccountAndLinksDialog() {
        DangerActionDialog dialog = DangerActionDialog.create();
        dialog.setTitleText("Delete Account and Links");
        dialog.setMessage("This will delete everything connected with given account.");
        dialog.setWarningText("Deleting Account is permanent. There is no way back. You're warned.");
        dialog.setConfirmationText("delete-all");
        dialog.setActionButtonText("Delete Account and Links");
        dialog.setActionButtonAction(this::deleteAccountAndLinks);
        return dialog;
    }

    private void deAuthSessions() {
        AppUtils.showSuccessNotification("Will do once implemented");
    }

    private void deleteAccount() {
        AppUtils.showSuccessNotification("Will do once implemented");
    }

    private void deleteAccountAndLinks() {
        AppUtils.showSuccessNotification("Will do once implemented");
    }
}
