package com.epam.laboratory.app.service;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.domain.BaseEntity;
import com.epam.laboratory.app.repository.EntityDao;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public abstract class AbstractEntityService<T extends BaseEntity> implements EntityService<T> {

    protected final EntityDao<T> dao;

    @Logging(Level.INFO)
    @Override
    public T registerNew(T entity) {
        prepareEntity(entity);
        return dao.save(entity);
    }

    @Logging(Level.INFO)
    @Override
    public T update(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }
        return dao.update(entity);
    }

    @Logging(Level.INFO)
    @Override
    public void delete(T entity) {
        dao.delete(entity);
    }

    @Logging(Level.INFO)
    @Transactional(readOnly = true)
    @Override
    public Optional<T> selectById(Long id, Class<T> entityClass) {
        return dao.findById(id, entityClass);
    }

    @Logging(Level.INFO)
    @Transactional(readOnly = true)
    @Override
    public Collection<T> selectByCondition(BiFunction<CriteriaBuilder, Root<T>, Predicate> condition, Class<T> entityClass) {
        return dao.findByCondition(condition, entityClass);
    }

    protected abstract void prepareEntity(T entity);
}
