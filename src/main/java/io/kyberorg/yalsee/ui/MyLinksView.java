package io.kyberorg.yalsee.ui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.ui.core.YalseeLayout;
import io.kyberorg.yalsee.utils.AppUtils;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.MY_LINKS_PAGE, layout = MainView.class)
@PageTitle("Yalsee: My Links")
public class MyLinksView extends YalseeLayout {

    private final Text sessionStub = new Text("");

    /**
     * Creates {@link MyLinksView}.
     */
    public MyLinksView() {
        setId(MyLinksView.class.getSimpleName());
        add(new H2("My Links Page"));
        add(sessionStub);
        init();
    }

    private void init() {
        String sessionId = AppUtils.getSessionId(VaadinSession.getCurrent());
        sessionStub.setText("Your session ID is: " + sessionId);
    }
}
