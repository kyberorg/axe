package io.kyberorg.yalsee.test.unit;

import io.kyberorg.yalsee.json.MattermostResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link MattermostResponse )}.
 *
 * @since 2.3
 */
public class MattermostResponseTests extends UnitTest {

    /**
     * Tests that valid JSON that Mattermost sends in response has icon, text and username.
     */
    @Test
    public void validJsonContainsIconTextAndUsername() {
        MattermostResponse mmJson = MattermostResponse.createWithText("https://yals.ee");
        assertTrue(StringUtils.isNotBlank(mmJson.getIconUrl()), "Icon is absent");
        assertTrue(GenericValidator.isUrl(mmJson.getIconUrl()), "Icon is not URL");

        assertTrue(StringUtils.isNotBlank(mmJson.getText()), "Text is absent");
        assertTrue(GenericValidator.isUrl(mmJson.getText()), "Text is not URL");
        assertTrue(StringUtils.isNotBlank(mmJson.getUsername()), "Username is absent");
    }

    /**
     * Tests that cannot replace icon with some random string that is not URL.
     */
    @Test
    public void cannotReplaceIconWithStringWhichIsNotUrl() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> MattermostResponse.createWithText("https://yals.ee")
                .replaceIconWith("Not an URL"));
    }

    /**
     * Tests than cannot construct {@link MattermostResponse} from text, that is not a URL.
     */
    @Test
    public void cannotAddTextWhenNotContainUrlOrError() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> MattermostResponse.createWithText("Just a text"));
    }

    /**
     * Tests than cannot add goto location from text, that is not a URL.
     */
    @Test
    public void cannotAddGoToLocationWhenItIsNotUrl() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> MattermostResponse.createWithText("https://yals.ee")
                .addGotoLocation("Not an URL"));
    }

    /**
     * Tests that Bot's username has software name (Yalsee).
     */
    @Test
    public void assertUserContainsYalsee() {
        MattermostResponse mmJson = MattermostResponse.createWithText("https://yals.ee");
        assertNotNull(mmJson.getUsername(), "Username is absent");
        assertTrue(mmJson.getUsername().contains("Yalsee"), "Username do not contain word 'Yalsee'");
    }

}
