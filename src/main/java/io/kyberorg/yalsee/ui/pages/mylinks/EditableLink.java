package io.kyberorg.yalsee.ui.pages.mylinks;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * This Component is combination of {@link Label} with Short Domain and editable {@link TextField} with ident.
 * It implements {@link HasValue} interface by proxying methods to editable part, which is {@link TextField}.
 *
 * @since 3.2
 */
@Tag("editable-link")
public class EditableLink extends Composite<HorizontalLayout> implements
        HasValue<EditableLink, String>, HasValue.ValueChangeEvent<String> {

    @Getter
    private final Label shortDomainPart;
    @Getter
    private final TextField editIdentField;

    private String oldValue;
    private HasValue<?, String> hasValue;
    private boolean isFromClient;

    /**
     * Creates object.
     *
     * @param shortDomain string with short domain (i.e. yls.ee)
     */
    public EditableLink(final String shortDomain) {
        this.shortDomainPart = new Label(shortDomain + "/");
        this.editIdentField = new TextField();

        getContent().add(shortDomainPart, editIdentField);

        editIdentField.addValueChangeListener(this::onValueChanged);
    }

    private void onValueChanged(final AbstractField.ComponentValueChangeEvent<TextField, String> e) {
        oldValue = e.getOldValue();
        hasValue = e.getHasValue();
        isFromClient = e.isFromClient();
    }

    @Override
    public void setValue(final String value) {
        editIdentField.setValue(value);
    }

    @Override
    public HasValue<?, String> getHasValue() {
        return hasValue;
    }

    @Override
    public boolean isFromClient() {
        return isFromClient;
    }

    @Override
    public String getOldValue() {
        return oldValue;
    }

    @Override
    public String getValue() {
        return editIdentField.getValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Registration addValueChangeListener(final ValueChangeListener listener) {
        return editIdentField.addValueChangeListener(listener);
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        editIdentField.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() {
        return editIdentField.isReadOnly();
    }

    @Override
    public void setRequiredIndicatorVisible(final boolean requiredIndicatorVisible) {
        editIdentField.setRequiredIndicatorVisible(requiredIndicatorVisible);
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return editIdentField.isRequiredIndicatorVisible();
    }

    /**
     * Defines if {@link #editIdentField} will be updated or not.
     *
     * @param newValue string with new value.
     * @return true - if {@link #editIdentField} value is differs from new one, false if values are same.
     */
    public boolean isCurrentValueDiffersFrom(final String newValue) {
        if (StringUtils.isBlank(this.oldValue)) return false;
        return !Objects.equals(this.oldValue, newValue);
    }
}
