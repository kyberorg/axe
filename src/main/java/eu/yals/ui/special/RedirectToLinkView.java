package eu.yals.ui.special;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.Endpoint;
import eu.yals.constants.Header;
import eu.yals.exception.NeedForRedirectException;
import eu.yals.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@SpringComponent
@UIScope
@Route(Endpoint.TNT.REDIRECTOR)
public class RedirectToLinkView extends VerticalLayout implements HasErrorParameter<NeedForRedirectException> {
    private static final String TAG = "[" + RedirectToLinkView.class.getSimpleName() + "]";

    public RedirectToLinkView() {
        add(new Text("Not intended for direct use. Needs parameter"));
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NeedForRedirectException> parameter) {
        String target = parameter.getCustomMessage();
        if (StringUtils.isNotBlank(target)) {
            VaadinResponse.getCurrent().setHeader(Header.LOCATION, AppUtils.covertUnicodeToAscii(target));
            return 302;
        } else {
            log.error("{} Target is empty", TAG);
            return 500;
        }

    }
}
