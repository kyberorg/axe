package pm.axe.ui.elements;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;

import java.util.stream.Stream;

/**
 * {@link Details} elements with list of requirements for username.
 */
public final class UsernameRequirements extends Composite<Details> implements HasStyle {

    /**
     * Creates new {@link UsernameRequirements}.
     *
     * @return created {@link UsernameRequirements}.
     */
    public static UsernameRequirements create() {
        return new UsernameRequirements();
    }

    /**
     * Sets {@link UsernameRequirements} visible.
     */
    public void show() {
        this.setVisible(true);
    }

    /**
     * Sets {@link UsernameRequirements} not visible.
     */
    public void hide() {
        this.setVisible(false);
    }

    private UsernameRequirements() {
        Details details = getContent();
        details.setSummaryText("Username requirements");
        Span span = new Span("Username should be");

        UnorderedList requirements = getRequirements();

        details.addContent(span, requirements);
        details.setOpened(false);
    }

    private UnorderedList getRequirements() {
        UnorderedList requirements = new UnorderedList();
        requirements.removeAll();
        Stream.of("The number of characters must be between 2 and 20.",
                        "Alphanumeric characters (a-zA-Z0-9), lowercase, or uppercase.",
                        "Also allowed of the dot (.), underscore (_), and hyphen (-).",
                        "The dot (.), underscore (_), or hyphen (-) must not be the first or last character.",
                        "The dot (.), underscore (_), or hyphen (-) doesn't appear consecutively, e.g., name..surname.")
                .forEach(requirement -> requirements.add(new ListItem(requirement)));
        return requirements;
    }
}
