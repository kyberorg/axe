package io.kyberorg.yalsee.models.dao.base;

import io.kyberorg.yalsee.models.TimeModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;

@RequiredArgsConstructor
@Repository
@Transactional
public class TimeRepositoryImpl<T extends TimeModel, ID> implements TimeRepository<T, ID> {

    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager em;

    public T save(T entity) {
        Assert.notNull(entity, "Entity must not be null.");

        if (entityInformation.isNew(entity)) {
            //we have defaults - no action needed
            em.persist(entity);
            return entity;
        } else {
            entity.setUpdated(TimeModel.now());
            return em.merge(entity);
        }
    }
}
