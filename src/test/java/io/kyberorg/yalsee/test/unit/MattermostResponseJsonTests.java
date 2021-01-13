package io.kyberorg.yalsee.test.unit;

import io.kyberorg.yalsee.json.MattermostResponseJson;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link MattermostResponseJson)}
 *
 * @since 2.3
 */
public class MattermostResponseJsonTests {

    @Test
    public void validJsonContainsIconTextAndUsername() {
        MattermostResponseJson mmJson = MattermostResponseJson.createWithText("https://yals.ee");
        assertTrue(StringUtils.isNotBlank(mmJson.getIconUrl()), "Icon is absent");
        assertTrue(GenericValidator.isUrl(mmJson.getIconUrl()), "Icon is not URL");

        assertTrue(StringUtils.isNotBlank(mmJson.getText()), "Text is absent");
        assertTrue(GenericValidator.isUrl(mmJson.getText()), "Text is not URL");
        assertTrue(StringUtils.isNotBlank(mmJson.getUsername()), "Username is absent");
    }

    @Test
    public void cannotReplaceIconWithStringWhichIsNotUrl() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> MattermostResponseJson.createWithText("https://yals.ee")
                .replaceIconWith("Not an URL"));
    }

    @Test
    public void cannotAddTextWhenNotContainUrlOrError() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> MattermostResponseJson.createWithText("Just a text"));
    }

    @Test
    public void cannotAddGoToLocationWhenItIsNotUrl() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> MattermostResponseJson.createWithText("https://yals.ee")
                .addGotoLocation("Not an URL"));
    }

    @Test
    public void assertUserContainsYalsee() {
        MattermostResponseJson mmJson = MattermostResponseJson.createWithText("https://yals.ee");
        assertNotNull(mmJson.getUsername(), "Username is absent");
        assertTrue(mmJson.getUsername().contains("Yalsee"), "Username do not contain word 'Yalsee'");
    }

}
