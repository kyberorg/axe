package io.kyberorg.yalsee.models.dao.base;

import io.kyberorg.yalsee.models.TimeModel;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TimeRepository<T extends TimeModel, ID> {
    /**
     * Saves a given entity and updates time fields accordingly.
     * Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param entity must not be {@literal null}.
     * @return the saved entity; will never be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal entity} is {@literal null}.
     */
    T save(T entity);
}
