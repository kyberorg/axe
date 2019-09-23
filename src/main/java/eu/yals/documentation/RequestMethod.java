package eu.yals.documentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Provides string value of method, which should be used to request certain REST-endpoint.
 * Used for documentation inside code.
 *
 * @since 2.0
 */
@Target(ElementType.FIELD)
public @interface RequestMethod {
    HttpMethod value();

    boolean api() default false;
}
