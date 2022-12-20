package pm.axe.ui.pages.err.page404;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import kong.unirest.HttpStatus;
import pm.axe.Endpoint;
import pm.axe.exception.PageNotFoundException;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.AxeBaseLayout;

@SpringComponent
@UIScope
@PageTitle("Page 404 - Axe.pm")
@Route(value = Endpoint.UI.PAGE_404, layout = MainView.class)
@CssImport("./css/error_views.css")
public class PageNotFoundPage extends AxeBaseLayout implements HasErrorParameter<PageNotFoundException> {

    private final H1 title = new H1();
    private final Span subTitle = new Span();
    private final Image image = new Image();

    /**
     * Creates {@link PageNotFoundPage}.
     */
    public PageNotFoundPage() {
        init();
        applyStyle();
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

    private void applyStyle() {
        image.addClassName("error-image");
        image.addClassName("centered-image");
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
        return HttpStatus.NOT_FOUND;
    }

    public static class IDs {
        public static final String VIEW_ID = "pageNotFoundView";
    }
}
