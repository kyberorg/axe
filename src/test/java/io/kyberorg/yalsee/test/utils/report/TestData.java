package io.kyberorg.yalsee.test.utils.report;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Test;

import java.util.Objects;

/**
 * Contains data about executed {@link Test}.
 *
 * @since 3.2.1
 */
@Data(staticConstructor = "create")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class TestData {
    @EqualsAndHashCode.Include
    private final String name;

    /**
     * This field set by {@link TestReport#reportTestFinished(TestSuite, TestData)} automatically.
     */
    @EqualsAndHashCode.Include
    private TestSuite testSuite;
    private TestResult testResult;
    private long timeTookMillis;

    private Throwable failCause;
    private String ignoreReason;
    private Throwable abortedCause;

    private TestData(final String testName) {
        this.name = testName;
    }

    @Override
    public String toString() {
        if (Objects.nonNull(testSuite)) {
            return this.testSuite + "." + this.name + "()";
        } else {
            return this.name + "()";
        }

    }
}
