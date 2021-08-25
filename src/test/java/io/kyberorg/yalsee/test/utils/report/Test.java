package io.kyberorg.yalsee.test.utils.report;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data(staticConstructor = "create")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Test {
    @EqualsAndHashCode.Include
    private final String name;
    private TestResult testResult;
    private String timeTook;


    private Test(final String testName) {
        this.name = testName;
    }

    @Override
    public String toString() {
        return this.name + "()";
    }
}
