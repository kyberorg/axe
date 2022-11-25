package pm.axe.ui.pages.err.appdown;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.*;
import org.springframework.transaction.CannotCreateTransactionException;
import pm.axe.Endpoint;
import pm.axe.constants.HttpCode;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.AxeBaseLayout;

@PageTitle("Axe.pm: Error 503")
@Route(value = Endpoint.UI.ERROR_PAGE_503, layout = MainView.class)
@CssImport("./css/error_views.css")
public class AppDownPage extends AxeBaseLayout implements HasErrorParameter<CannotCreateTransactionException> {

    private final H1 title = new H1();
    private final Span subTitle = new Span();
    private final Image image = new Image();

    /**
     * Creates {@link AppDownPage}.
     */
    public AppDownPage() {
        init();
        add(title, subTitle, image);
        this.setAlignItems(Alignment.CENTER);
    }

    private void init() {
        title.setText("Application is DOWN");
        subTitle.setText("Sorry, but application have hard time and could not serve any requests. "
                + "We are already aware of problem. Please come again shortly. ");

        image.setSrc("images/503.png");
        image.setAlt("Error 503 Image");

        image.addClassName("error-image");
        image.setHeight(image.getWidth());
    }

    @Override
    public int setErrorParameter(final BeforeEnterEvent event,
                                 final ErrorParameter<CannotCreateTransactionException> parameter) {
        return HttpCode.APP_IS_DOWN;
    }
}
