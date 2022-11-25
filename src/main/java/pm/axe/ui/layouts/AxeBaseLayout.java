package pm.axe.ui.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Getter;

/**
 * Core Layout for all pages.
 *
 * @since 3.0.7
 */
public class AxeBaseLayout extends HorizontalLayout {
    private final Div leftDiv = new Div();
    @Getter
    private final VerticalLayout centralLayout = new VerticalLayout();
    private final Div rightDiv = new Div();

    /**
     * Initialise Layout elements and styles them.
     */
    public AxeBaseLayout() {
        init();
        applyStyle();
    }

    private void init() {
        super.add(leftDiv, centralLayout, rightDiv);
    }

    private void applyStyle() {
        addClassName("axe-layout");

        leftDiv.addClassName("responsive-div");
        centralLayout.addClassName("responsive-center");
        rightDiv.addClassName("responsive-div");

        centralLayout.addClassNames("main-area", "border");
    }

    @Override
    public void add(final Component... components) {
        centralLayout.add(components);
    }

    @Override
    public void removeAll() {
        centralLayout.removeAll();
    }
}
