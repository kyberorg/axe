package eu.yals.test.utils;

import org.junit.runner.Description;
import org.testcontainers.lifecycle.TestDescription;

/**
 * Class that converts JUnit {@link Description} to TestContainers{@link TestDescription}
 *
 * @since 2.6
 */
public class YalsTestDescription implements TestDescription {
    private String testId;
    private String testName;

    public static TestDescription fromDescription(Description description) {
        YalsTestDescription t = new YalsTestDescription();
        t.testId = description.getMethodName();
        t.testName = description.getTestClass().getSimpleName();
        return t;
    }

    @Override
    public String getTestId() {
        return this.testId;
    }

    @Override
    public String getFilesystemFriendlyName() {
        return this.testName + "-" + this.testId;
    }
}
