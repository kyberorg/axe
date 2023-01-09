package pm.axe.ui.elements;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.Locale;

/**
 * {@link VaadinIcon#COPY} {@link Icon}, which copies to Clipboard.
 */
@JsModule("./js/copy-to-clipboard.js")
public class CopyToClipboardIcon extends Composite<Icon> implements HasStyle {
    private static final String copyIcon = VaadinIcon.COPY.name().
            toLowerCase(Locale.ENGLISH).replace('_', '-');

    public CopyToClipboardIcon() {
        getContent().getElement().setAttribute("icon", "vaadin" + ":" + copyIcon);

        getContent().getElement().removeAttribute("onclick");
        getContent().getElement().setAttribute("onclick", "copyTextToClipboard(this)");
    }

    /**
     * Sets text copy and triggers JS function from `copy-to-clipboard.js`, which copies text to clipboard.
     *
     * @param textToCopy string with text to copy.
     */
    public void setTextToCopy(final String textToCopy) {
        getContent().getElement().removeAttribute("text");
        getContent().getElement().setAttribute("text", textToCopy);
    }

    /**
     * Performs programmatic click.
     */
    public void click() {
        getContent().getElement().callJsFunction("click");
    }
}
