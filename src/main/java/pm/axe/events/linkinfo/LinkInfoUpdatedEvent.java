package pm.axe.events.linkinfo;

import lombok.Data;
import pm.axe.db.models.LinkInfo;

@Data(staticConstructor = "createWith")
public class LinkInfoUpdatedEvent {
    private final LinkInfo linkInfo;
}
