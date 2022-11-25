package pm.axe.events.link;

import lombok.Data;
import pm.axe.db.models.Link;

@Data(staticConstructor = "createWith")
public class LinkSavedEvent {
    private final Link link;
}
