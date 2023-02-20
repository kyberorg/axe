package pm.axe.ui.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * Layout, that uses only part of {@link AxeBaseLayout#getCentralLayout()} and its width depends on its content.
 */
@CssImport(value = "./css/axe_compact_styles.css")
public class AxeCompactLayout extends AxeBaseLayout {
    @Getter(AccessLevel.PROTECTED) private final VerticalLayout compactLayout = new VerticalLayout();

    /**
     * Creates {@link AxeCompactLayout}.
     */
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
    public void add(final Component... components) {
        compactLayout.add(components);
    }

    @Override
    public void removeAll() {
        compactLayout.removeAll();
    }
}
