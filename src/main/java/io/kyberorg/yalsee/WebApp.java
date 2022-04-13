package io.kyberorg.yalsee;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Push
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@PWA(
        name = "Yalsee - the link shortener",
        shortName = "yalsee",
        offlinePath = "offline-page.html",
        offlineResources = {"images/logo.png", "Pebble-Regular.woff"},
        description = "Yalsee - the link shortener")
@Theme(themeClass = Lumo.class, variant = Lumo.LIGHT)
public class WebApp implements AppShellConfigurator {

}
