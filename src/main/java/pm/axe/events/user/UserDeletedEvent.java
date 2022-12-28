package pm.axe.events.user;

import lombok.Data;
import pm.axe.db.models.User;

@Data(staticConstructor = "createWith")
public class UserDeletedEvent {
    private final User deletedUser;
}
