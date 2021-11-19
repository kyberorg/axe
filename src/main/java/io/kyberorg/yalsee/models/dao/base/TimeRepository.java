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
     * Saves new or updates current record. Also updates "updated" field and save entity.
     *
     * @param entity not null model to save
     */
    default T saveOrUpdate(T entity) {
        //noinspection unchecked
        if (entity.getId() != null && existsById((ID) entity.getId())) {
            entity.setUpdated(now());
        }
        return save(entity);
    }
}
