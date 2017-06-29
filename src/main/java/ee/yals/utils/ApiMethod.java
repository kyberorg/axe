package ee.yals.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Class description
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
 */
@Target(ElementType.FIELD)
public @interface ApiMethod {
    HttpMethod value();
}
