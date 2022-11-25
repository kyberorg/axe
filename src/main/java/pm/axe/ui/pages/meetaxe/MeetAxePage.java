package pm.axe.ui.pages.meetaxe;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import pm.axe.Endpoint;
import pm.axe.ui.MainView;
import pm.axe.ui.elements.Code;
import pm.axe.ui.layouts.AxeBaseLayout;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.MEET_AXE_PAGE, layout = MainView.class)
@PageTitle("Axe.pm: Meet Axe")
@CssImport("./css/meet_axe_page.css")
public class MeetAxePage extends AxeBaseLayout {
    private final Image image = new Image();
    private final H2 title = new H2();
    private final Span firstLine = new Span();
    private final H4 whyTitle = new H4();
    private final Span reasonZero = new Span();
    private final Span reasonOne = new Span();
    private final Span reasonTwo = new Span();
    private final H4 whatChangedTitle = new H4();
    private final Span changeZero = new Span();
    private final Span changeOne = new Span();
    private final H4 howAffectsMeTitle = new H4();
    private final Span howAffectsMeText = new Span();
    private final H4 oldDomainNoteTitle = new H4();
    private final Span endLine = new Span();

    private final Code yalseeBig = new Code("Yals.ee");
    private final Code yalseeSmall = new Code("yals.ee");
    private final Code axeBig = new Code("Axe.pm");
    private final Code axeSmall = new Code("axe.pm");
    private final Code ylsee = new Code("yls.ee");

    /**
     * Creates {@link MeetAxePage}.
     */
    public MeetAxePage() {
        init();
        applyStyle();

        Span oldDomainNoteLine = getOldDomainNoteLine();
        Span needLongerLine = needLongerLine();
        Span emptyLine = new Span();

        add(image, title, firstLine,
                whyTitle, reasonZero, reasonOne, reasonTwo,
                whatChangedTitle, changeZero, changeOne,
                howAffectsMeTitle, howAffectsMeText,
                oldDomainNoteTitle, oldDomainNoteLine, needLongerLine,
                emptyLine, endLine);
        this.setAlignItems(Alignment.CENTER);
    }

    private void init() {
        image.setSrc("images/yalsee2axe.png");
        image.setAlt("Yalsee -> Axe Image");

        title.setText("World, meet Axe");

        firstLine.setText("As may notice this project changed title, branding and domain. " +
                "Shorter links - better Internet!");

        whySection();
        whatChangedSection();
        howAffectsMeSection();
        oldDomainSection();

        endLine.setText("Start chopping your long links with Axe!");
    }

    private void applyStyle() {
        image.addClassName("page-wide-image");
        image.addClassName("centered-image");
    }

    private void whySection() {
        whyTitle.setText("# But Why?");
        Code yalseeBig = new Code("Yals.ee"); //private is now working for some reason.
        Span reasonZeroText = new Span(" was an abbreviation from Yet another Link Shortener. " +
                "Now it has grown into a pretty functional fully-fledged link shortener.  " +
                "It is not yet another anymore, it is one of the best in its area.");
        reasonZero.add(yalseeBig, reasonZeroText);
        reasonOne.add("It was unclear how to spell Yalsee (with Y or J) at the start.");

        Span reasonTwoPartOne = new Span("For short period of time stupid scammers used my project for " +
                "their criminal activities. This was enough for some BigTechs to ban ");
        Span and = new Span(" and ");
        Code ylsee = new Code("yls.ee"); //private is now working for some reason.
        Span reasonTwoLastPart = new Span(" links. So a new domain will solve this issue as well.");
        reasonTwo.add(reasonTwoPartOne, yalseeSmall, and, ylsee, reasonTwoLastPart);
    }

    private void whatChangedSection() {
        whatChangedTitle.setText("# What has changed?");

        Span changeZeroFirstPart = new Span("Domain. ");
        Span changeZeroSecondPart = new Span(" is one-character shorter.");

        changeZero.add(changeZeroFirstPart, axeBig, changeZeroSecondPart);
        changeOne.add("Got rid of Google Analytics and the requirement to show annoying Cookie Banner.");
    }

    private void howAffectsMeSection() {
        howAffectsMeTitle.setText("# How it affects me?");

        Span howAffectsFirstPart = new Span("I'd suggest updating links and bookmarks. Just replace ");
        Code ylsee = new Code("yls.ee"); //private is now working for some reason.
        Span with = new Span(" with ");
        Span howAffectsLastPart = new Span(" and that's it.");
        howAffectsMeText.add(howAffectsFirstPart, ylsee, with, axeSmall, howAffectsLastPart);
    }

    private void oldDomainSection() {
        oldDomainNoteTitle.setText("# How long will the old domain remain?");
    }

    private Span getOldDomainNoteLine() {
        Span yalseeExpirationNote = new Span(" will expire at 8.3.2024 ");
        Span and = new Span("and ");
        Span ylseeExpirationNote = new Span(" will stay until 6.1.2025. ");

        return new Span(yalseeBig, yalseeExpirationNote, and, ylsee, ylseeExpirationNote);
    }

    private Span needLongerLine() {
        Span needThemLonger = new Span("Need them for longer period of time? Contact me by Email: ");
        Anchor email = new Anchor("mailto:alex@kyberorg.io", "alex at kyberorg dot io");
        Span slash = new Span("/");
        Span telegramStr = new Span("Telegram: ");
        Anchor telegram = new Anchor("https://t.me/kyberorg", "@kyberorg");
        return new Span(needThemLonger, email, slash, telegramStr, telegram);
    }
}
