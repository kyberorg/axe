package pm.axe.ui.pages.user.profile.tabs;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import pm.axe.db.models.User;
import pm.axe.internal.HasTabInit;

@RequiredArgsConstructor
@SpringComponent
@UIScope
public class DangerZoneTab extends VerticalLayout implements HasTabInit {

    @Override
    public void tabInit(final User user) {
        add(new Span("This is Danger Zone"));
    }
}
