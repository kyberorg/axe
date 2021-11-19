package io.kyberorg.yalsee.models.dao.base;

import io.kyberorg.yalsee.models.TimeModel;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;

@NoRepositoryBean
@Transactional
public class TimeRepositoryImpl<T extends TimeModel, ID> extends SimpleJpaRepository<T, ID>
        implements TimeRepository<T, ID> {

    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager em;

    public TimeRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.em = entityManager;
    }

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
