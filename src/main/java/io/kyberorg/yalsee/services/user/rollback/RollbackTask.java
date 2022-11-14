package io.kyberorg.yalsee.services.user.rollback;

import io.kyberorg.yalsee.models.BaseModel;
import lombok.Data;

/**
 * Combination of Model and Record to delete during rollback process.
 */
@Data(staticConstructor = "create")
public class RollbackTask {
    private final Class<? extends BaseModel> model;
    private final BaseModel record;

    public String toString() {
        return RollbackTask.class.getSimpleName() + "( model=" + model.getSimpleName() + ", id=" + record.getId() + ")";
    }
}
