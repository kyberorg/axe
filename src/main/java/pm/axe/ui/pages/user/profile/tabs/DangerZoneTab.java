package pm.axe.ui.pages.user.profile.tabs;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import pm.axe.db.models.User;
import pm.axe.internal.HasTabInit;
import pm.axe.ui.elements.Section;
import pm.axe.utils.VaadinUtils;


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
        deleteAccountSection.getTitle().addClassName("red");
        deleteAccountSection.getTitle().addClassName("bold");

        Button deleteAccountOnlyButton = new Button("Delete Account");
        deleteAccountOnlyButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteAccountOnlyButton.addClickListener(this::onDeleteAccount);

        Button deleteAccountAndLinksButton = new Button("Delete Account and Links");
        deleteAccountAndLinksButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteAccountAndLinksButton.addClickListener(this::onDeleteAccountAndLinks);

        HorizontalLayout layout = new HorizontalLayout(deleteAccountOnlyButton, deleteAccountAndLinksButton);
        VaadinUtils.fitLayoutInWindow(layout);
        return layout;
    }

    private void onDeleteAccount(final ClickEvent<Button> event) {
        if (user == null) return; //no user - no action
        Notification.show("Will do once they implement me");
    }

    private void onDeleteAccountAndLinks(final ClickEvent<Button> event) {
        if (user == null) return; //no user - no action
        Notification.show("Will do once they implement me");
    }
}
