package pm.axe.test.utils.report;

import lombok.Data;

import static pm.axe.test.TestApp.Constants.HASH_CODE_INITIAL_RESTART;

/**
 * Encapsulates test {@link Class}. Main purpose is remove common {@link #TEST_ROOT_PACKAGE} from reports.
 *
 * @since 3.2.1
 */
@Data(staticConstructor = "create")
public class TestSuite {
    private static final String TEST_ROOT_PACKAGE = "pm.axe.test";

    private final Class<?> suiteClazz;

    /**
     * Creates {@link TestSuite} object from {@link Class}.
     *
     * @param clazz test class.
     */
    public TestSuite(final Class<?> clazz) {
        this.suiteClazz = clazz;
    }

    /**
     * Get test suite class name without any packages.
     *
     * @return string with test suite class name.
     */
    public String getClassName() {
        return suiteClazz.getSimpleName();
    }

    @Override
    public String toString() {
        String fullPackage = suiteClazz.getPackageName();
        return fullPackage.replace(TEST_ROOT_PACKAGE + ".", "") + "." + suiteClazz.getSimpleName();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TestSuite other)) {
            return false;
        }
        if (this.getSuiteClazz() != null && other.getSuiteClazz() != null) {
            return this.getSuiteClazz().getSimpleName().equals(other.getSuiteClazz().getSimpleName());
        } else {
            return other.getSuiteClazz() == null;
        }
    }

    @Override
    public int hashCode() {
        int result = HASH_CODE_INITIAL_RESTART;
        if (this.getSuiteClazz() != null) {
            result = result + this.getSuiteClazz().hashCode();
        }
        return result;
    }

}
