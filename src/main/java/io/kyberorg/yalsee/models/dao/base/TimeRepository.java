package io.kyberorg.yalsee.models.dao.base;

import io.kyberorg.yalsee.models.TimeModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import static io.kyberorg.yalsee.models.TimeModel.now;

/**
 * Time aware {@link CrudRepository}.
 *
 * @param <T>  not null model that implements {@link TimeModel} interface.
 * @param <ID> type of ID field.
 */
@NoRepositoryBean
public interface TimeRepository<T extends TimeModel, ID> extends CrudRepository<T, ID> {
    /**
     * Updates "updated" field and save entity.
     *
     * @param entity not null model to save
     */
    default void saveAndUpdateTime(T entity) {
        entity.setUpdated(now());
        save(entity);
    }
}
