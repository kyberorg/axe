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
                    Retry retry = method.getAnnotation(Retry.class);
                    if (retry != null) {
                        base.evaluate();
                    } else {
                        throw t;
                    }
                }
            }
        };
    }
}
