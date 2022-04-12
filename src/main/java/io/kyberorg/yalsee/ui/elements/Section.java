package io.kyberorg.yalsee.ui.elements;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.apache.commons.lang3.StringUtils;

public class Section extends Composite<VerticalLayout> {
    private final H5 title = new H5();
    private final VerticalLayout content = new VerticalLayout();

    public Section() {
        this("");
    }

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

    public void setTitle(final String text) {
        title.setText(text);
    }

    public void setContent(final Component... components) {
        content.removeAll();
        content.add(components);
    }

    public void setCustomContent(final Component customLayout) {
        getContent().replace(content, customLayout);
    }

    public void add(final Component component) {
        content.add(component);
    }

    public void setCustomTitleElement(final Component customTitle) {
        getContent().removeAll();
        getContent().add(customTitle);
        getContent().add(content);

        //setting content closer to title
        content.getStyle().set("padding-top", "0");
    }
}
