package io.kyberorg.yalsee.ui.elements;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.apache.commons.lang3.StringUtils;

/**
 * Bordered element with title and content.
 */
public class Section extends Composite<VerticalLayout> {
    private final H5 title = new H5();
    private final VerticalLayout content = new VerticalLayout();

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
            getContent().add(title);
            //setting content closer to title
            content.getStyle().set("padding-top", "0");
        }

        content.setClassName("section-content");
        getContent().add(content);
        getContent().addClassName("border");
        getContent().addClassName("color-border");
        getContent().addClassName("section");
    }

    /**
     * Sets title.
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
        getContent().removeAll();
        getContent().add(customTitle);
        getContent().add(content);

        //setting content closer to title
        content.getStyle().set("padding-top", "0");
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
        getContent().replace(content, customLayout);
    }

    /**
     * Adds elements to section body.
     *
     * @param component Vaadin Component to add.
     */
    public void add(final Component component) {
        content.add(component);
    }
}
