package io.kyberorg.yalsee.test.utils.report;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Encapsulates test {@link Class}. Main purpose is remove common {@link #TEST_ROOT_PACKAGE} from reports.
 *
 * @since 3.2.1
 */
@Data(staticConstructor = "create")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TestSuite {
    private static final String TEST_ROOT_PACKAGE = "io.kyberorg.yalsee.test";

    @EqualsAndHashCode.Include
    private final Class<?> suiteClazz;

    /**
     * Creates {@link TestSuite} object from {@link Class}.
     *
     * @param clazz test class.
     */
    public TestSuite(final Class<?> clazz) {
        this.suiteClazz = clazz;
    }

    @Override
    public String toString() {
        String fullPackage = suiteClazz.getPackageName();
        return fullPackage.replace(TEST_ROOT_PACKAGE + ".", "") + "." + suiteClazz.getSimpleName();
    }

}
