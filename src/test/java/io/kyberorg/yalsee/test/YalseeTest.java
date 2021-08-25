package io.kyberorg.yalsee.test;

import io.kyberorg.yalsee.test.utils.TestWatcherExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Global methods.
 */
@ExtendWith(TestWatcherExtension.class) // catching test results and logging results to System.out
public abstract class YalseeTest {

    @BeforeAll
    public static void init() {
        TestInstance.getInstance().init();
    }

    @AfterAll
    public static void afterSuite() {
        System.out.println("Suite completed...");
    }

    private static class TestInstance {
        private static TestInstance instance = null;
        private static boolean welcomeExecuted = false;
        private static boolean afterTestsExecuted = false;

        public static TestInstance getInstance() {
            if (instance == null) {
                instance = new TestInstance();
            }
            return instance;
        }

        public void init() {
            if (welcomeExecuted) return;

            System.out.println("Testing started");
            addAsShutdownHook(this::afterTests);
            welcomeExecuted = true;
        }

        public void afterTests() {
            if (afterTestsExecuted) return;

            System.out.println("Testing is completed");
            afterTestsExecuted = true;
        }

        private void addAsShutdownHook(final Runnable method) {
            Runtime.getRuntime().addShutdownHook(new Thread(method));
        }
    }
}
