package eu.yals.ui;

import com.github.appreciated.app.layout.annotations.Caption;
import com.github.appreciated.app.layout.annotations.Icon;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.Endpoint;
import liquibase.pro.packaged.D;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.FREE_HOME, layout = AppView.class)
@Caption("Free Home View")
@Icon(VaadinIcon.AIRPLANE)
@CssImport("./css/free_home_view.css")
@PageTitle("Link shortener for friends: Free Vaadin")
public class FreeHomeView extends HorizontalLayout {

    /**
     * Creates {@link FreeHomeView}.
     */
    public FreeHomeView() {
        setId(FreeHomeView.class.getSimpleName());
        init();
    }

    private void init() {
        Div leftDiv = new Div();
        VerticalLayout center = new VerticalLayout();
        Div rightDiv = new Div();

        //styles for Divs
        leftDiv.addClassNames("test-div", "color-div");
        rightDiv.addClassNames("test-div", "color-div");

        //styles for Central
        center.addClassNames("test-center", "color-center");

        //

        add(leftDiv, center, rightDiv);
    }
}
