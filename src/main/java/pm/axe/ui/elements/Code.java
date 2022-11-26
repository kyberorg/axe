package pm.axe.ui.elements;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import lombok.NoArgsConstructor;

/**
 * Code Tag Element.
 */
@NoArgsConstructor
@Tag("code")
public class Code extends HtmlContainer implements ClickNotifier<Code> {

    /**
     * Creates {@link Code} element from collection of {@link Component}s.
     *
     * @param components element's children
     */
    public Code(final Component... components) {
        super(components);
    }

    /**
     * Creates {@link Code} element with given text.
     *
     * @param text element's text.
     */
    public Code(final String text) {
        this.setText(text);
    }
}
