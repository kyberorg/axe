package pm.axe.ui.elements;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * {@link VaadinIcon#COPY} {@link Icon}, which copies to Clipboard.
 */
@JsModule("./js/copy-to-clipboard.js")
public class CopyToClipboardIcon extends Composite<Icon> implements HasStyle {
    public CopyToClipboardIcon() {
        getContent().getElement().setAttribute("icon", "vaadin" + ":" + VaadinIcon.COPY);
    }

    /**
     * Sets text copy and triggers JS function from `copy-to-clipboard.js`, which copies text to clipboard.
     *
     * @param textToCopy string with text to copy.
     */
    public void setTextToCopy(final String textToCopy) {
        getContent().getElement().removeAttribute("onclick");
        getContent().getElement().setAttribute("onclick", "copyToClipboard('" + textToCopy +"')");
    }
}
