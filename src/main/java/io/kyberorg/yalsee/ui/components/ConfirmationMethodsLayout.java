package io.kyberorg.yalsee.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import io.kyberorg.yalsee.ui.user.RegistrationView;

/**
 * Layout for Confirmation Methods section in {@link RegistrationView}.
 *
 * @since 4.0
 */
public class ConfirmationMethodsLayout extends Composite<FormLayout> {

    public void addItemWithLabel(final String label, final Component... items) {
        Div itemWrapper = new Div();

        // Wrap the given items into a single div
        itemWrapper.add(items);

        // getContent() returns a wrapped FormLayout
        getContent().addFormItem(itemWrapper, label);
    }
}
