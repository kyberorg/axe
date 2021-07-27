package io.kyberorg.yalsee.ui.core;

import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;

public class EditableLink extends AbstractSinglePropertyField<TextField, String> {
    @Getter private final Text shortDomainPart;
    @Getter private final TextField editIdentField;

    public EditableLink(final String shortDomain) {
        super("", "", false);
        this.shortDomainPart = new Text(shortDomain + "/");
        this.editIdentField = new TextField();

        HorizontalLayout content = new HorizontalLayout();
        content.add(shortDomainPart, editIdentField);
    }
}
