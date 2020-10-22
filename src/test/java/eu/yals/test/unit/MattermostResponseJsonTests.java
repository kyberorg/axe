package eu.yals.test.unit;

import eu.yals.json.MattermostResponseJson;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link MattermostResponseJson)}
 *
 * @since 2.3
 */
public class MattermostResponseJsonTests {

    @Test
    public void validJsonContainsIconTextAndUsername() {
        MattermostResponseJson mmJson = MattermostResponseJson.createWithText("https://yals.eu");
        assertTrue("Icon is absent", StringUtils.isNotBlank(mmJson.getIconUrl()));
        assertTrue("Icon is not URL", GenericValidator.isUrl(mmJson.getIconUrl()));

        assertTrue("Text is absent", StringUtils.isNotBlank(mmJson.getText()));
        assertTrue("Text is not URL", GenericValidator.isUrl(mmJson.getText()));
        assertTrue("Username is absent", StringUtils.isNotBlank(mmJson.getUsername()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotReplaceIconWithStringWhichIsNotUrl() {
        MattermostResponseJson.createWithText("https://yals.eu")
                .replaceIconWith("Not an URL");
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotAddTextWhenNotContainUrlOrError() {
        MattermostResponseJson.createWithText("Just a text");
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotAddGoToLocationWhenItIsNotUrl() {
        MattermostResponseJson.createWithText("https://yals.eu")
                .addGotoLocation("Not an URL");
    }

    @Test
    public void assertUserContainsYals() {
        MattermostResponseJson mmJson = MattermostResponseJson.createWithText("https://yals.eu");
        assertNotNull("Username is absent", mmJson.getUsername());
        assertTrue("Username do not contain word 'Yals'", mmJson.getUsername().contains("Yals"));
    }

}
