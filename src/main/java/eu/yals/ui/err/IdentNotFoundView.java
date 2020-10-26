package eu.yals.ui.err;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.Endpoint;
import eu.yals.exception.IdentNotFoundException;
import eu.yals.ui.AppView;

import static eu.yals.constants.HttpCode.STATUS_404;

@SpringComponent
@UIScope
@PageTitle("Yals: Error 404")
@Route(value = Endpoint.UI.PAGE_404, layout = AppView.class)
public class IdentNotFoundView extends VerticalLayout implements HasErrorParameter<IdentNotFoundException> {

    private final H1 title = new H1();
    private final Span subTitle = new Span();
    private final Image image = new Image();

    /**
     * Creates {@link IdentNotFoundView}.
     */
    public IdentNotFoundView() {
        init();
        add(title, subTitle, image);
        this.setAlignItems(Alignment.CENTER);
    }

    private void init() {
        this.setId(IDs.VIEW_ID);

        title.setText("404 - No Such Link Exception");
        subTitle.setText("We don't have long link that match your short link. "
                + "Make sure you copypasted it fully and without extra characters");

        image.setSrc("images/404.jpg");
        image.setAlt("Error 404 Image");
    }

    /**
     * Set 404 status.
     *
     * @param event     internal Vaadin event
     * @param parameter optional param. We ignore it here
     * @return HTTP Code for page not found aka 404
     */
    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<IdentNotFoundException> parameter) {
        return STATUS_404;
    }

    public static class IDs {
        public static final String VIEW_ID = "identNotFoundView";
    }
}
