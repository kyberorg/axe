package pm.axe.ui.pages.redirect;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import kong.unirest.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import pm.axe.Axe;
import pm.axe.Endpoint;
import pm.axe.exception.NeedForRedirectException;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.AxeBaseLayout;
import pm.axe.utils.AppUtils;
import pm.axe.utils.UrlUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringComponent
@UIScope
@CssImport("./css/common_styles.css")
@CssImport("./css/redirect_view.css")
@Route(value = Endpoint.TNT.REDIRECT_PAGE, layout = MainView.class)
@PageTitle("Link Info - Axe.pm")
public class RedirectPage extends AxeBaseLayout implements HasErrorParameter<NeedForRedirectException> {
    private static final String TAG = "[" + RedirectPage.class.getSimpleName() + "]";

    private final Span directAccessBanner = new Span("Not intended for direct use");
    private final VerticalLayout redirectPage = new VerticalLayout();

    private final Span firstTextLine = new Span("According to our records");

    private final Span originLine = new Span();
    private final Anchor originLink = new Anchor();
    private final Span lenDiffText = new Span();

    private final Span secondTextLine = new Span("is short link for");
    private final Anchor targetLink = new Anchor();

    private final Span redirectLine = new Span();
    private final Span rdrPreText = new Span("You will be redirected in ");
    private final Span rdrCounter = new Span();
    private final Span rdrUnit = new Span(" seconds... ");
    private final Span rdrClickText = new Span("or click ");
    private final Anchor rdrHereLink = new Anchor();
    private final Span rdrPostText = new Span(", if you too busy to wait.");

    private final Span nbLine = new Span();
    private final Span nb = new Span("NB! ");
    private final Span nbPreText = new Span("You can add ");
    private final Span nbSymbol = new Span();
    private final Span nbPostText = new Span(" symbol to your short link to bypass this page.");

    private final Page page = UI.getCurrent().getPage();
    private FeederThread thread;

    private String origin;
    private String target = "/"; //this is for redirecting to main page in case someone visits page directly.

    private final AppUtils appUtils;

    /**
     * Creates {@link RedirectPage}.
     *
     * @param appUtils application utils for getting settings.
     */
    public RedirectPage(final AppUtils appUtils) {
        this.appUtils = appUtils;

        init();
        applyStyle();
    }

    private void init() {
        setId(IDs.VIEW_ID);
        directAccessBanner.setId(IDs.DIRECT_ACCESS_BANNER);
        redirectPage.setId(IDs.REDIRECT_PAGE);
        originLink.setId(IDs.ORIGIN_LINK_ID);
        lenDiffText.setId(IDs.LEN_DIFF_ID);
        targetLink.setId(IDs.TARGET_LINK_ID);
        rdrCounter.setId(IDs.COUNTER_ID);
        rdrHereLink.setId(IDs.HERE_LINK_ID);
        nb.setId(IDs.NB);
        nbSymbol.setId(IDs.BYPASS_SYMBOL_ID);

        rdrCounter.setText(appUtils.getRedirectPageTimeout() + "");
        rdrHereLink.setText("here");
        nbSymbol.setText(appUtils.getRedirectPageBypassSymbol());

        originLine.add(originLink, lenDiffText);
        redirectLine.add(rdrPreText, rdrCounter, rdrUnit, rdrClickText, rdrHereLink, rdrPostText);
        nbLine.add(nb, nbPreText, nbSymbol, nbPostText);

        redirectPage.add(firstTextLine, originLine, secondTextLine, targetLink, redirectLine, nbLine);
        add(directAccessBanner, redirectPage);

        redirectPage.setVisible(false);
    }

    private void applyStyle() {
        redirectPage.setWidthFull();
        nb.addClassName("bold");
    }

    @Override
    public int setErrorParameter(final BeforeEnterEvent event,
                                 final ErrorParameter<NeedForRedirectException> parameter) {
        String message = parameter.getCustomMessage();
        String[] parts = message.split(Axe.C.URL_SAFE_SEPARATOR);
        if (parts.length != 2) {
            log.error("Something wrong with received message {} it's length {}, but only 2 parts are excepted",
                    message, parts.length);
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        this.origin = appUtils.getShortUrl() + "/" + parts[0];
        this.target = parts[1];

        if (shouldSkipRedirectPage()) {
            log.info("{} skipping redirect page for {}", TAG, this.origin);
            return doHeaderRedirect(target);
        }

        originLink.setText(this.origin);
        originLink.setHref(this.origin + appUtils.getRedirectPageBypassSymbol());

        //intercepting click event
        // just because in Vaadin 23+ unable to modify VaadinResponse after it send, so doHeaderRedirect fails
        originLink.getElement().addEventListener("click", e -> doJSRedirect(target));

        lenDiffText.setText(makeLengthDifferenceText());

        targetLink.setText(this.target);
        targetLink.setHref(this.target);

        rdrHereLink.setHref(this.target);

        directAccessBanner.setVisible(false);
        redirectPage.setVisible(true);
        return HttpStatus.OK;
    }

    private boolean shouldSkipRedirectPage() {
        //TODO add if link has owner -> true
        boolean isTargetInternal = appUtils.isInternalUrl(this.target);
        boolean hasBypassSymbol = appUtils.hasRedirectPageBypassSymbol(this.origin);
        return isTargetInternal || hasBypassSymbol;
    }

    private void doJSRedirect(final String target) {
        if (Objects.nonNull(this.page)) {
            this.page.setLocation(UrlUtils.covertUnicodeUrlToAscii(target));
        }
    }

    private int doHeaderRedirect(final String target) {
        if (StringUtils.isNotBlank(target)) {
            VaadinResponse.getCurrent().setHeader(Axe.Headers.LOCATION, UrlUtils.covertUnicodeUrlToAscii(target));
            return HttpStatus.TEMPORARY_REDIRECT;
        } else {
            log.error("{} Target is empty", TAG);
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private String makeLengthDifferenceText() {
        int lenDiff = this.target.length() - this.origin.length();
        String adjective;

        if (lenDiff > 0) {
            adjective = "shorter";
        } else if (lenDiff < 0) {
            adjective = "longer";
        } else {
            adjective = "same length";
        }

        String lenLine;
        if (lenDiff == 0) {
            lenLine = String.format(" (%s)", adjective);
        } else {
            int lenDifference = lenDiff > 0 ? lenDiff : Math.abs(lenDiff);
            String ch = lenDiff == 1 ? "char" : "chars";
            lenLine = String.format(" (%d %s %s)", lenDifference, ch, adjective);
        }
        return lenLine;
    }

    @Override
    protected void onAttach(final AttachEvent attachEvent) {
        thread = new FeederThread(attachEvent.getUI(), this);
        thread.start();
    }

    @Override
    protected void onDetach(final DetachEvent detachEvent) {
        thread.interrupt();
        thread = null;
    }

    private static final class FeederThread extends Thread {
        private final UI ui;
        private final RedirectPage view;

        private int count = 0;

        FeederThread(final UI ui, final RedirectPage view) {
            this.ui = ui;
            this.view = view;
        }

        @Override
        public void run() {
            try {
                int redirectTimeout = view.appUtils.getRedirectPageTimeout();
                while (count < redirectTimeout) {
                    TimeUnit.SECONDS.sleep(1);
                    count++;

                    int secondsRemains = redirectTimeout - count;
                    ui.access(() -> view.rdrCounter.setText(secondsRemains + ""));
                }
                ui.access(() -> view.doJSRedirect(view.target));
            } catch (InterruptedException e) {
                log.error("{} while waiting for redirect", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    public static class IDs {
        public static final String VIEW_ID = "redirectView";
        public static final String DIRECT_ACCESS_BANNER = "directAccessBanner";
        public static final String REDIRECT_PAGE = "redirectPage";
        public static final String ORIGIN_LINK_ID = "originLink";
        public static final String LEN_DIFF_ID = "lenDiff";
        public static final String TARGET_LINK_ID = "targetLink";
        public static final String HERE_LINK_ID = "hereLink";
        public static final String BYPASS_SYMBOL_ID = "bypassSymbol";
        public static final String COUNTER_ID = "counter";
        public static final String NB = "nb";
    }
}
