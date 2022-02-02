package io.kyberorg.yalsee.test.utils;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.Dimension;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.title;

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
     * Just more readable alias for Selenide's {@link com.codeborne.selenide.Selenide#title()}.
     *
     * @return string with title of opened page
     */
    public static String getPageTitle() {
        return title();
    }

    /**
     * Asserts that image element contains image not-squared image (width != height).
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
