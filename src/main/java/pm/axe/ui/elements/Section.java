package pm.axe.ui.elements;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * Bordered element with title and content.
 */
public class Section extends Composite<VerticalLayout> implements HasStyle {
    @Getter
    private final H5 title = new H5();
    private final VerticalLayout content = new VerticalLayout();

    private final VerticalLayout self = getContent();

    /**
     * Creates Section without title.
     */
    public Section() {
        this("");
    }

    /**
     * Create Section with given title.
     *
     * @param titleText string with section title.
     */
    public Section(final String titleText) {
        if (StringUtils.isNotBlank(titleText)) {
            title.setText(titleText);
            self.add(title);
            setContentCloserToTitle();
        }

        content.setClassName("section-content");
        self.add(content);
        self.addClassName("border");
        self.addClassName("color-border");
        self.addClassName("section");
    }

    /**
     * Sets the given string as the text of title component.
     *
     * @param text string with title.
     */
    public void setTitle(final String text) {
        title.setText(text);
    }

    /**
     * Sets custom element as title. Replaces current title.
     *
     * @param customTitle custom element to replace default title.
     */
    public void setCustomTitleElement(final Component customTitle) {
        self.removeAll();
        self.add(customTitle);
        self.add(content);
        setContentCloserToTitle();
    }

    /**
     * Adds elements to section body. Replaces all current elements.
     *
     * @param components Vaadin components to add.
     */
    public void setContent(final Component... components) {
        content.removeAll();
        content.add(components);
    }

    /**
     * Replace content with given custom layout.
     *
     * @param customLayout layout to set as content.
     */
    public void setCustomContent(final Component customLayout) {
        self.replace(content, customLayout);
    }

    /**
     * Adds elements to section body.
     *
     * @param component Vaadin Component to add.
     */
    public void add(final Component component) {
        content.add(component);
    }

    public void setCentered() {
        self.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setWidth("auto");
    }

    private void setContentCloserToTitle() {
        content.getStyle().set("padding-top", "0");
    }
}
