package ee.yals.test.selenide.debug;

import static com.codeborne.selenide.Selenide.$;

public class Core {

    public static void clickIt() {
        $("#shortenIt").click();
    }
}
