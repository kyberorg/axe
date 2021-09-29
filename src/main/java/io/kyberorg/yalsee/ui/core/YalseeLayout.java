package io.kyberorg.yalsee.ui.core;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Core Layout for all pages.
 *
 * @since 3.0.7
 */
public class YalseeLayout extends HorizontalLayout {
    private final HorizontalLayout page = new HorizontalLayout();
    private final Div leftDiv = new Div();
    private final VerticalLayout centralLayout = new VerticalLayout();
    private final Div rightDiv = new Div();

    /**
     * Initialise Layout elements and styles them.
     */
    public YalseeLayout() {
        init();
        applyStyle();
    }

    private void init() {
        super.add(leftDiv, centralLayout, rightDiv);
    }

    private void applyStyle() {
        page.setWidthFull();

        leftDiv.addClassName("responsive-div");
        centralLayout.addClassName("responsive-center");
        rightDiv.addClassName("responsive-div");

        centralLayout.addClassNames("main-area", "border");
    }

    @Override
    public void add(final Component... components) {
        centralLayout.add(components);
    }
}
