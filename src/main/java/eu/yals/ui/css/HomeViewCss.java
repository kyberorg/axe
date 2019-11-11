package eu.yals.ui.css;

import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.springframework.stereotype.Component;

@Component
public class HomeViewCss {

    public void applyRowStyle(HasStyle row) {
        row.getStyle().set("margin-right", "-15px");
        row.getStyle().set("margin-left", "-15px");
    }

    public void applyMainAreaStyle(HasStyle mainArea) {
        mainArea.getStyle().set("background", "#f0f0f0");
        applyBorder(mainArea);
    }

    public void applyResultAreaStyle(HasStyle resultArea) {
        resultArea.getStyle().set("background", "#dff0d8");
        resultArea.getStyle().set("font-size", "xx-large");
        resultArea.getStyle().set("margin-top", "15px");
        applyBorder(resultArea);
    }

    public void applyQrCodeAreaStyle(HasStyle qrCodeArea) {
        qrCodeArea.getStyle().set("text-align", "center");
        applyBorder(qrCodeArea);
    }

    public void applyFooterStyle(HorizontalLayout footer) {
        footer.getStyle().set("position", "absolute");
        footer.getStyle().set("bottom", "0");
        footer.getStyle().set("height", "60px");
        footer.getStyle().set("background-color", "#f5f5f5");

        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        footer.setAlignItems(FlexComponent.Alignment.CENTER);
    }

    public void makeSubtitleItalic(HasStyle subtitle) {
        subtitle.getStyle().set("font-style", "italic");
    }

    public void makeLinkStrong(HasStyle link) {
        link.getStyle().set("font-weight", "700");
        link.getStyle().set("color", "#337ab7");
        link.getStyle().set("background-color", "transparent");
    }

    public void paintVersion(HasStyle version) {
        version.getStyle().set("color", "#777");
    }

    private void applyBorder(HasStyle component) {
        component.getStyle().set("border", "1px solid #e3e3e3");
        component.getStyle().set("border-radius", "4px");
    }


}
