package eu.yals.test.ui.vaadin;

import com.vaadin.testbench.annotations.RunOnHub;
import eu.yals.test.TestApp;
import eu.yals.test.ui.vaadin.elements.HomeViewElement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

//@RunOnHub
@RunOnHub("ci.yadev.eu")
public class PocIT extends VaadinTest<HomeViewElement> {

    private final static int SERVER_PORT = Integer.parseInt(System.getProperty(TestApp.Properties.SERVER_PORT, "8080"));
    private final static String LOCAL_URL = String.format("http://host.testcontainers.internal:%d", SERVER_PORT);
    protected final static String BASE_URL = System.getProperty(TestApp.Properties.TEST_URL, LOCAL_URL);

    @Before
    public void setup() throws Exception {
        super.setup();
        getDriver().get(BASE_URL);
    }

    @Override
    protected HomeViewElement openView() {
        return $(HomeViewElement.class).waitForFirst();
    }

    @Test
    public void testVaadin() {
        HomeViewElement homeView = $(HomeViewElement.class).first();
        String title = homeView.title().getText();
        Assert.assertEquals("Yet another link shortener", title);
    }
}
