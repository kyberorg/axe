package io.kyberorg.yalsee.ui.pages.err.ident404;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.exception.IdentNotFoundException;
import io.kyberorg.yalsee.ui.MainView;
import io.kyberorg.yalsee.ui.layouts.YalseeLayout;

@SpringComponent
@UIScope
@PageTitle("Yalsee: Error 404")
@Route(value = Endpoint.UI.IDENT_404, layout = MainView.class)
@CssImport("./css/error_views.css")
public class IdentNotFoundPage extends YalseeLayout implements HasErrorParameter<IdentNotFoundException> {

    private final H1 title = new H1();
    private final Span subTitle = new Span();
    private final Image image = new Image();

    /**
     * Creates {@link IdentNotFoundPage}.
     */
    public IdentNotFoundPage() {
        init();
        applyStyle();
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

    private void applyStyle() {
        image.addClassName("error-image");
        image.addClassName("centered-image");
    }

    /**
     * Set 404 status.
     *
     * @param event     internal Vaadin event
     * @param parameter optional param. We ignore it here
     * @return HTTP Code for page not found aka 404
     */
    @Override
    public int setErrorParameter(final BeforeEnterEvent event, final ErrorParameter<IdentNotFoundException> parameter) {
        return HttpCode.NOT_FOUND;
    }

    public static class IDs {
        public static final String VIEW_ID = "identNotFoundView";
    }
}