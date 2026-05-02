package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.domain.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;

@Repository
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public abstract class AbstractDao<T extends Entity> implements Dao<T> {

    @PersistenceContext
    protected EntityManager em;

    @Logging(Level.INFO)
    @Transactional(readOnly = true)
    @Override
    public Optional<T> findById(Long id, Class<T> clazz) {
        return Optional.ofNullable(em.find(clazz, id));
    }

    @Logging(Level.INFO)
    @Transactional(readOnly = true)
    @Override
    public Collection<T> findByCondition(BiFunction<CriteriaBuilder, Root<T>, Predicate> condition, Class<T> clazz) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(clazz);
        Root<T> root = cq.from(clazz);
        Predicate predicate = condition.apply(cb, root);
        cq.select(root).where(predicate);
        return em.createQuery(cq).getResultList();
    }

    @Logging(Level.INFO)
    @Override
    public T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }
        if (entity.getId() != null) {
            return update(entity);
        }
        em.persist(entity);
        return entity;
    }

    @Logging(Level.INFO)
    @Override
    public T update(T entity) {
        if (entity == null || entity.getId() == null) {
            throw new IllegalArgumentException("Entity must not be null and must have an ID");
        }
        var managedEntity = em.find(entity.getClass(), entity.getId());
        if (managedEntity == null) {
            throw new IllegalArgumentException("Entity with ID " + entity.getId() + " does not exist");
        }
        return em.merge(entity);
    }

    @Logging(Level.INFO)
    @Override
    public void  delete(T entity) {
        if (entity == null || entity.getId() == null) {
            throw new IllegalArgumentException("Entity must not be null and must have an ID");
        }
        var managedEntity = em.find(entity.getClass(), entity.getId());
        if (managedEntity != null) {
            em.remove(managedEntity);
            em.flush();
        }
    }
}
