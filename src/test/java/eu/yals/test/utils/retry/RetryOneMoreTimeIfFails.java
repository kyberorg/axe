package eu.yals.test.utils.retry;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RetryOneMoreTimeIfFails {
}
