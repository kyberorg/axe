package io.kyberorg.yalsee.ui.special;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.constants.Header;
import io.kyberorg.yalsee.exception.NeedForRedirectException;
import io.kyberorg.yalsee.ui.MainView;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static io.kyberorg.yalsee.constants.HttpCode.*;

@SuppressWarnings("FieldCanBeLocal")
@Slf4j
@SpringComponent
@UIScope
@Route(value = Endpoint.TNT.REDIRECTOR, layout = MainView.class)
public class RedirectToLinkView extends VerticalLayout implements HasErrorParameter<NeedForRedirectException> {
    private static final String TAG = "[" + RedirectToLinkView.class.getSimpleName() + "]";

    private FeederThread thread;
    private final Span origSpan = new Span();
    private final Span targetSpan = new Span();

    private final Span counterSpan = new Span();
    private final Span counterText = new Span("Redirecting in ");
    private final Span counterNum = new Span();

    private final Page page = UI.getCurrent().getPage();

    private String origin = null;
    private String target = null;

    private final AppUtils appUtils;

    /**
     * Creates {@link RedirectToLinkView}.
     */
    public RedirectToLinkView(AppUtils appUtils) {
        this.appUtils = appUtils;

        counterSpan.add(counterText, counterNum);
        add(origSpan, targetSpan, counterSpan);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        thread = new FeederThread(attachEvent.getUI(), this);
        thread.start();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        thread.interrupt();
        thread = null;
    }

    @Override
    public int setErrorParameter(final BeforeEnterEvent event,
                                 final ErrorParameter<NeedForRedirectException> parameter) {
        String message = parameter.getCustomMessage();
        String[] parts = message.split(App.URL_SAFE_SEPARATOR);
        if (parts.length != 2) {
            log.error("Something wrong with received message {} it's length {}, but only 2 parts are excepted",
                    message, parts.length);
            return STATUS_500;
        }

        //TODO if user - doRedirect right now
        this.origin = appUtils.getShortUrl() + "/" + parts[0];
        this.target = parts[1];

        origSpan.setText("Origin is: " + this.origin);
        targetSpan.setText("target is: " + this.target);

        return STATUS_200;
    }

    private void doJSRedirect(String target) {
        if (Objects.nonNull(this.page)) {
            this.page.setLocation(AppUtils.covertUnicodeToAscii(target));
        }
    }

    private int doHeaderRedirect(String target) {
        if (StringUtils.isNotBlank(target)) {
            VaadinResponse.getCurrent().setHeader(Header.LOCATION, AppUtils.covertUnicodeToAscii(target));
            return STATUS_302;
        } else {
            log.error("{} Target is empty", TAG);
            return STATUS_500;
        }
    }

    private static final class FeederThread extends Thread {
        private final UI ui;
        private final RedirectToLinkView view;

        private int count = 0;

        public FeederThread(UI ui, RedirectToLinkView view) {
            this.ui = ui;
            this.view = view;
        }

        @Override
        public void run() {
            try {
                int redirectTimeout = view.appUtils.getRedirectPageTimeout();
                while (count < redirectTimeout ) {
                    TimeUnit.SECONDS.sleep(1);
                    count++;

                    int secondsRemains = redirectTimeout - count;
                    ui.access(() -> view.counterNum.setText(secondsRemains + " seconds"));
                }
                ui.access(() -> view.doJSRedirect(view.target));
            } catch (InterruptedException e) {
                log.error("{} while waiting for redirect", e.getMessage());
            }
        }
    }
}
