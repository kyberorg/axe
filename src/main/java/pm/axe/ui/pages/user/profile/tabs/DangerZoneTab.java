package pm.axe.ui.pages.user.profile.tabs;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import pm.axe.db.models.User;
import pm.axe.internal.HasTabInit;
import pm.axe.ui.elements.Section;


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
        Component content = deleteAccountSectionContent();
        deleteAccountSection.setContent(content);
    }

    private Component deleteAccountSectionContent() {
        Button deleteAccountButton = new Button();
        deleteAccountButton.setText("Delete this Account");
        deleteAccountButton.addClickListener(this::onDeleteAccount);

        HorizontalLayout layout = new HorizontalLayout(deleteAccountButton);
        layout.setAlignItems(Alignment.BASELINE);
        return layout;
    }

    private void onDeleteAccount(final ClickEvent<Button> event) {
        if (user == null) return; //no user - no action
        Notification.show("TODO");
    }
}
