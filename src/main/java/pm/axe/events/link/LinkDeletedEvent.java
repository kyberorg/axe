package pm.axe.events.link;

import lombok.Data;
import pm.axe.db.models.Link;

@Data(staticConstructor = "createWith")
public class LinkDeletedEvent {
    private final Link link;
}
