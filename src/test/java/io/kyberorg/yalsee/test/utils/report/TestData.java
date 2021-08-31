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

    private Throwable onFireReason;
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

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TestData)) {
            return false;
        }
        TestData other = (TestData) o;

        if (this.getName() != null && this.getTestSuite() != null) {
            return this.getName().equals(other.getName()) && this.getTestSuite().equals(other.getTestSuite());
        } else if (this.getName() != null) {
            return this.getName().equals(other.getName()) && other.getTestSuite() == null;
        } else if (this.getTestSuite() != null) {
            return other.getName() == null && this.getTestSuite().equals(other.getTestSuite());
        } else {
            return other.getName() == null && other.getTestSuite() == null;
        }


    }

    @Override
    public int hashCode() {
        int result = 17;
        if (this.getName() != null) {
            result = result + this.getName().hashCode();
        }
        if (this.getTestSuite() != null) {
            result = result + this.getTestSuite().hashCode();
        }
        return result;
    }
}
