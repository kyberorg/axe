package io.kyberorg.yalsee.test.utils.report;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TestReport {
    private static TestReport singleInstance;

    private final HashMap<TestSuite, List<Test>> reportMap = new HashMap<>();

    private final Counter failedTestsCounter = new Counter();
    private final Counter passedTestsCounter = new Counter();
    private final Counter ignoredTestsCounter = new Counter();
    private final Counter abortedTestsCounter = new Counter();
    private final Counter suitesCounter = new Counter();

    public static TestReport getInstance() {
        if (singleInstance == null) {
            singleInstance = new TestReport();
        }
        return singleInstance;
    }

    private TestReport() {
    }

    public int countFailedTests() {
        return failedTestsCounter.getValue();
    }

    public int countPassedTests() {
        return passedTestsCounter.getValue();
    }

    public int countIgnoredTests() {
        return ignoredTestsCounter.getValue();
    }

    public int countAbortedTests() {
        return abortedTestsCounter.getValue();
    }

    public int countSuites() {
        return suitesCounter.getValue();
    }

    public int countCompletedTests() {
        return countPassedTests() + countFailedTests() + countIgnoredTests() + countAbortedTests();
    }

    public List<Test> getFailedTests() {
        return getTestsByResult(TestResult.FAILED);
    }

    public List<Test> getPassedTests() {
        return getTestsByResult(TestResult.PASSED);
    }

    public List<Test> getIgnoredTests() {
        return getTestsByResult(TestResult.IGNORED);
    }

    public List<Test> getAbortedTests() {
        return getTestsByResult(TestResult.ABORTED);
    }

    public Duration getTotalTimeSpent() {
        List<Test> allTests = new ArrayList<>();
        reportMap.values().forEach(allTests::addAll);
        long totalTimeMillis = allTests.stream().map(Test::getTimeTookMillis).reduce(0L, Long::sum);
        return Duration.ofMillis(totalTimeMillis);
    }

    public void reportTestFinished(TestSuite testSuite, Test test) {
        List<Test> testsList;
        if (reportMap.containsKey(testSuite)) {
            testsList = reportMap.get(testSuite);
        } else {
            //new suite
            testsList = new ArrayList<>();
            suitesCounter.increment();
        }
        if (!testsList.isEmpty() && testsList.contains(test)) {
            //element already exists - skipping
            return;
        }
        //attaching linked testSuite
        test.setTestSuite(testSuite);
        //element not exists in list - adding
        testsList.add(test);
        reportMap.putIfAbsent(testSuite, testsList);
        updateCounters(test);
    }

    private List<Test> getTestsByResult(final TestResult result) {
        List<Test> allTests = new ArrayList<>();
        reportMap.values().forEach(allTests::addAll);
        return allTests.stream().filter(test -> test.getTestResult() == result).collect(Collectors.toList());
    }

    private void updateCounters(final Test test) {
        switch (test.getTestResult()) {
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
        }
    }

    public static class Counter {
        private final AtomicInteger counter = new AtomicInteger(0);

        public int getValue() {
            return counter.get();
        }

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
