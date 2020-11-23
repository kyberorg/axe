package eu.yals.test.utils.retry;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class RetryRule implements TestRule {
    @Override
    public Statement apply(Statement base, Description method) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } catch (Throwable t) {
                    RetryOneMoreTimeIfFails retry = method.getAnnotation(RetryOneMoreTimeIfFails.class);
                    if (retry != null) {
                        base.evaluate();
                    } else if(method.getTestClass().getAnnotation(RetryOneMoreTimeIfFails.class) != null) {
                        System.out.println("Retrying one more time...");
                        base.evaluate();
                    } else {
                        throw t;
                    }
                }
            }
        };
    }
}
