package eu.yals.json;

import lombok.Data;

/**
 * JSON without fields. Can be used for testing.
 *
 * @since 1.0
 */
@Data(staticConstructor = "create")
public class EmptyJson implements YalsJson {
}
