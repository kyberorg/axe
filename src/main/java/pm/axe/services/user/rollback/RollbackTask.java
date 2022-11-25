package pm.axe.services.user.rollback;

import pm.axe.db.models.BaseModel;
import lombok.Data;

/**
 * Combination of Model and Record to delete during rollback process.
 */
@Data(staticConstructor = "create")
public class RollbackTask {
    private final Class<? extends BaseModel> model;
    private final BaseModel record;

    @Override
    public String toString() {
        return RollbackTask.class.getSimpleName() + "(model=" + model.getSimpleName() + ",id=" + record.getId() + ")";
    }
}
