package io.kyberorg.yalsee.test.utils.report;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data(staticConstructor = "create")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Test {
    @EqualsAndHashCode.Include
    private final String name;

    /**
     * This field set by {@link TestReport#reportTestFinished(TestSuite, Test)} automatically.
     */
    @EqualsAndHashCode.Include
    private TestSuite testSuite;
    private TestResult testResult;
    private long timeTookMillis;

    private Throwable failCause;
    private String ignoreReason;
    private Throwable abortedCause;


    private Test(final String testName) {
        this.name = testName;
    }

    @Override
    public String toString() {
        return this.name + "()";
    }
}
