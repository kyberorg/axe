package pm.axe.test.utils;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.Dimension;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

/**
 * Useful utils for Selenide tests.
 *
 * @since 2.7.4
 */
public final class SelenideUtils {
    private SelenideUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Waits until Site Loads (Body tag appears).
     *
     * @param durationInSeconds wait duration in seconds.
     */
    public static void waitUntilSiteLoads(final int durationInSeconds) {
        $("body").shouldBe(visible, Duration.ofSeconds(durationInSeconds));
    }

    /**
     * Asserts that element contains not-squared image (width != height).
     *
     * @param image element with image.
     */
    public static void assertThatImageIsNotSquared(final SelenideElement image) {
        Assertions.assertTrue(image.isImage(), "Element is not image");
        Dimension imageDimensions = image.getSize();
        Assertions.assertTrue(imageDimensions.getWidth() != imageDimensions.getHeight(),
                String.format("Excepted: not squared image, Got: %dx%d square",
                        imageDimensions.getWidth(), imageDimensions.getHeight()));
    }
}
