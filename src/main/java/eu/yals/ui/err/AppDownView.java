package eu.yals.ui.err;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import eu.yals.Endpoint;
import eu.yals.ui.MainView;
import org.springframework.transaction.CannotCreateTransactionException;

import static eu.yals.constants.HttpCode.STATUS_503;

@PageTitle("Yals: Error 503")
@Route(value = Endpoint.UI.ERROR_PAGE_503, layout = MainView.class)
public class AppDownView extends VerticalLayout implements HasErrorParameter<CannotCreateTransactionException> {

    private final H1 title = new H1();
    private final Span subTitle = new Span();
    private final Image image = new Image();

    /**
     * Creates {@link AppDownView}.
     */
    public AppDownView() {
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
    }

    @Override
    public int setErrorParameter(final BeforeEnterEvent event,
                                 final ErrorParameter<CannotCreateTransactionException> parameter) {
        return STATUS_503;
    }
}
