package io.kyberorg.yalsee.test.utils.report;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data(staticConstructor = "create")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TestSuite {
    private static final String TEST_ROOT_PACKAGE = "io.kyberorg.yalsee.test";

    @EqualsAndHashCode.Include
    private final Class<?> suiteClazz;

    public TestSuite(Class<?> clazz) {
        this.suiteClazz = clazz;
    }

    @Override
    public String toString() {
        String fullPackage = suiteClazz.getPackageName();
        return fullPackage.replace(TEST_ROOT_PACKAGE + ".", "") + "." + suiteClazz.getSimpleName();
    }

}
