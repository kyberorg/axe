package io.kyberorg.yalsee.test.utils.report;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Holds info about tests. Saves all info to {@link HashMap} {@link #reportMap},
 * has counters to avoid iterating {@link Map} when only counters are needed.
 *
 * @since 3.2.1
 */
public final class TestReport {
    private static TestReport singleInstance;

    private final HashMap<TestSuite, List<TestData>> reportMap = new HashMap<>();

    private final Counter failedTestsCounter = new Counter();
    private final Counter passedTestsCounter = new Counter();
    private final Counter ignoredTestsCounter = new Counter();
    private final Counter abortedTestsCounter = new Counter();
    private final Counter onFireTestsCounter = new Counter();
    private final Counter suitesCounter = new Counter();
    private final Counter brokenSuitesCounter = new Counter();

    /**
     * Provides {@link TestReport} object. One object per application.
     *
     * @return same {@link TestReport} object for all.
     */
    public static synchronized TestReport getReport() {
        if (singleInstance == null) {
            singleInstance = new TestReport();
        }
        return singleInstance;
    }

    private TestReport() {
    }

    /**
     * Number of {@link TestResult#FAILED} tests.
     *
     * @return number of failed tests.
     */
    public int countFailedTests() {
        return failedTestsCounter.getValue();
    }

    /**
     * Number of {@link TestResult#PASSED} tests.
     *
     * @return number of passed tests.
     */
    public int countPassedTests() {
        return passedTestsCounter.getValue();
    }

    /**
     * Number of {@link TestResult#IGNORED} tests.
     *
     * @return number of ignored tests.
     */
    public int countIgnoredTests() {
        return ignoredTestsCounter.getValue();
    }

    /**
     * Number of {@link TestResult#ABORTED} tests.
     *
     * @return number of aborted tests.
     */
    public int countAbortedTests() {
        return abortedTestsCounter.getValue();
    }

    /**
     * Number of {@link TestResult#ON_FIRE} tests.
     *
     * @return number of on fire tests.
     */
    public int countOnFireTests() {
        return onFireTestsCounter.getValue();
    }

    /**
     * Number of Test Suites aka Test classes, where at least one test run.
     *
     * @return number of test classes started or completed.
     */
    public int countSuites() {
        return suitesCounter.getValue();
    }

    /**
     * Number of Suites aka Test classes, where at @AfterAll crashed with Exception or Error.
     *
     * @return number of test classes where execution was broken
     */
    public int countBrokenSuites() {
        return brokenSuitesCounter.getValue();
    }

    /**
     * Add broken suite to its counter.
     */
    public void markSuiteAsBroken() {
        brokenSuitesCounter.increment();
    }

    /**
     * Number of completed tests. Result doesn't matter.
     *
     * @return number of finished tests with any result.
     */
    public int countCompletedTests() {
        return countPassedTests() + countFailedTests() + countIgnoredTests() + countAbortedTests()
                + countOnFireTests();
    }

    /**
     * Gets {@link TestResult#FAILED} tests.
     *
     * @return list of {@link TestData} of failed tests.
     */
    public List<TestData> getFailedTests() {
        return getTestsByResult(TestResult.FAILED);
    }

    /**
     * Gets {@link TestResult#PASSED} tests.
     *
     * @return list of {@link TestData} of passed tests.
     */
    public List<TestData> getPassedTests() {
        return getTestsByResult(TestResult.PASSED);
    }

    /**
     * Gets {@link TestResult#IGNORED} tests.
     *
     * @return list of {@link TestData} of ignored tests.
     */
    public List<TestData> getIgnoredTests() {
        return getTestsByResult(TestResult.IGNORED);
    }

    /**
     * Gets {@link TestResult#ABORTED} tests.
     *
     * @return list of {@link TestData} of aborted tests.
     */
    public List<TestData> getAbortedTests() {
        return getTestsByResult(TestResult.ABORTED);
    }

    /**
     * Gets {@link TestResult#ON_FIRE} tests.
     *
     * @return list of {@link TestData} of tests, that on fire.
     */
    public List<TestData> getOnFireTests() {
        return getTestsByResult(TestResult.ON_FIRE);
    }

    /**
     * Count time took to run all tests. Sum of each individual test times.
     *
     * @return {@link Duration} of time spent to run all tests.
     */
    public Duration getTotalTimeSpent() {
        List<TestData> allTests = new ArrayList<>();
        reportMap.values().forEach(allTests::addAll);
        long totalTimeMillis = allTests.stream().map(TestData::getTimeTookMillis).reduce(0L, Long::sum);
        return Duration.ofMillis(totalTimeMillis);
    }

    /**
     * Report that test is completed, its suite, result and other {@link TestData}.
     *
     * @param testSuite suite aka test class of completed test.
     * @param testData  {@link TestData} for completed test.
     */
    public synchronized void reportTestFinished(final TestSuite testSuite, final TestData testData) {
        //attaching linked testSuite
        testData.setTestSuite(testSuite);

        List<TestData> testsList;
        if (reportMap.containsKey(testSuite)) {
            testsList = reportMap.get(testSuite);
        } else {
            //new suite
            testsList = new ArrayList<>();
            suitesCounter.increment();
        }
        if (!testsList.isEmpty() && testsList.contains(testData)) {
            //element already exists - skipping
            return;
        }

        //element not exists in list - adding
        testsList.add(testData);
        reportMap.putIfAbsent(testSuite, testsList);
        updateCounters(testData);
    }

    private List<TestData> getTestsByResult(final TestResult result) {
        List<TestData> allTests = new ArrayList<>();
        reportMap.values().forEach(allTests::addAll);
        return allTests.stream().filter(testData -> testData.getTestResult() == result).collect(Collectors.toList());
    }

    private void updateCounters(final TestData testData) {
        if (testData.getTestResult() == null) return;
        switch (testData.getTestResult()) {
            case PASSED:
                passedTestsCounter.increment();
                break;
            case FAILED:
                failedTestsCounter.increment();
                break;
            case IGNORED:
                ignoredTestsCounter.increment();
                break;
            case ABORTED:
                abortedTestsCounter.increment();
                break;
            case ON_FIRE:
            default:
                onFireTestsCounter.increment();
                break;
        }
    }

    /**
     * Atomic Counter for counting tests.
     */
    public static class Counter {
        private final AtomicInteger counter = new AtomicInteger(0);

        /**
         * Gets actual value.
         *
         * @return current counter value.
         */
        public int getValue() {
            return counter.get();
        }

        /**
         * Increments current counter value.
         */
        public void increment() {
            while (true) {
                int existingValue = getValue();
                int newValue = existingValue + 1;
                if (counter.compareAndSet(existingValue, newValue)) {
                    return;
                }
            }
        }
    }
}
