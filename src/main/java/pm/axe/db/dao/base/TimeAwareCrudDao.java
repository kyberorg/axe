package pm.axe.db.dao.base;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import pm.axe.db.models.TimeModel;

/**
 * Time aware {@link CrudRepository}.
 *
 * @param <T>  not null model that implements {@link TimeModel} interface.
 * @param <ID> type of ID field.
 * @since 3.7
 */
@NoRepositoryBean
public interface TimeAwareCrudDao<T extends TimeModel, ID> extends CrudRepository<T, ID> {
    /**
     * Updates current record. Also updates "updated" field and save entity.
     *
     * @param entity not null model to save
     * @return stored entity
     */
    default T update(T entity) {
        //noinspection unchecked
        if (entity.getId() != null && existsById((ID) entity.getId())) {
            entity.setUpdated(TimeModel.now());
        }
        return save(entity);
    }
}
