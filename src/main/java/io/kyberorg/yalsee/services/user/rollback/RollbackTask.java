package io.kyberorg.yalsee.services.user.rollback;

import io.kyberorg.yalsee.models.BaseModel;
import lombok.Data;

/**
 * //TODO
 */
@Data(staticConstructor = "create")
public class RollbackTask {
    private final Class<? extends BaseModel> model;
    private final long recordId;

    public String getName() {
        return RollbackTask.class.getSimpleName() + "[" + model.getSimpleName() + " (ID " + recordId + ")]";
    }
}
