package io.kyberorg.yalsee.ui.err;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.exception.PageNotFoundException;
import io.kyberorg.yalsee.ui.MainView;

import static io.kyberorg.yalsee.constants.HttpCode.STATUS_404;


@SpringComponent
@UIScope
@PageTitle("Yalsee: Error 404")
@Route(value = Endpoint.UI.PAGE_404, layout = MainView.class)
@CssImport("./css/error_views.css")
public class PageNotFoundView extends VerticalLayout implements HasErrorParameter<PageNotFoundException> {

    private final H1 title = new H1();
    private final Span subTitle = new Span();
    private final Image image = new Image();

    /**
     * Creates {@link PageNotFoundView}.
     */
    public PageNotFoundView() {
        init();
        add(title, subTitle, image);
        this.setAlignItems(Alignment.CENTER);
    }

    private void init() {
        this.setId(IDs.VIEW_ID);

        title.setText("404 - No Such Page Exception");
        subTitle.setText("We don't have such page at our site. Really.");

        image.setSrc("images/404.jpg");
        image.setAlt("Error 404 Image");
    }

    /**
     * Sets 404 status.
     *
     * @param event     Vaadin internal event
     * @param parameter optional parameter during rerouting. We ignore it here.
     * @return http code for not found aka 404
     */
    @Override
    public int setErrorParameter(final BeforeEnterEvent event, final ErrorParameter<PageNotFoundException> parameter) {
        return STATUS_404;
    }

    public static class IDs {
        public static final String VIEW_ID = "pageNotFoundView";
    }
}
