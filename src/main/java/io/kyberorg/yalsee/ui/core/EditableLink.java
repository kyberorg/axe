package io.kyberorg.yalsee.ui.core;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

@Tag("editable-link")
public class EditableLink extends Composite<HorizontalLayout> implements HasValue<EditableLink, String>, HasValue.ValueChangeEvent<String> {
    @Getter private final Text shortDomainPart;
    @Getter private final TextField editIdentField;

    public EditableLink(final String shortDomain) {
        this.shortDomainPart = new Text(shortDomain + "/");
        this.editIdentField = new TextField();

        getContent().add(shortDomainPart, editIdentField);
    }

    @Override
    public void setValue(String value) {
        editIdentField.setValue(value);
    }

    @Override
    public HasValue<?, String> getHasValue() {
        return null;
    }

    @Override
    public boolean isFromClient() {
        return false;
    }

    @Override
    public String getOldValue() {
        return editIdentField.getValue();
    }

    @Override
    public String getValue() {
        return editIdentField.getValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Registration addValueChangeListener(ValueChangeListener listener) {
        return editIdentField.addValueChangeListener(listener);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        editIdentField.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() {
        return editIdentField.isReadOnly();
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        editIdentField.setRequiredIndicatorVisible(requiredIndicatorVisible);
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return editIdentField.isRequiredIndicatorVisible();
    }
}
