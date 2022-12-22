package pm.axe.ui.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Compact Layout.
 */
@CssImport(value = "./css/axe_compact_styles.css")
public class AxeCompactLayout extends AxeBaseLayout {
    private final VerticalLayout compactLayout = new VerticalLayout();

    public AxeCompactLayout() {
        init();
        applyStyle();
    }

    private void init() {
        super.add(compactLayout);
    }

    private void applyStyle() {
        compactLayout.setClassName("axe-compact");
        compactLayout.addClassName("border");
    }

    @Override
    public void add(Component... components) {
        compactLayout.add(components);
    }

    @Override
    public void removeAll() {
        compactLayout.removeAll();
    }
}
