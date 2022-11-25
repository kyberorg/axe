package pm.axe.events.link;

import pm.axe.db.models.Link;
import lombok.Data;

@Data(staticConstructor = "createWith")
public class LinkDeletedEvent {
    private final Link link;
}
