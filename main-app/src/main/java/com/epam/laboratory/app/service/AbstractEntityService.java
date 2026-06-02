package com.epam.laboratory.app.service;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.domain.Entity;
import com.epam.laboratory.app.repository.EntityDao;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.function.BiFunction;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public abstract class AbstractEntityService<T extends Entity> implements EntityService<T> {

    protected final EntityDao<T> dao;

    @Logging(Level.INFO)
    @Transactional(readOnly = true)
    @Override
    public Collection<T> selectByCondition(BiFunction<CriteriaBuilder, Root<T>, Predicate> condition) {
        return dao.findByCondition(condition);
    }

    @Logging(Level.INFO)
    @Transactional(readOnly = true)
    @Override
    public long count() {
        return dao.count();
    }

    @Logging(Level.INFO)
    @Transactional(readOnly = true)
    @Override
    public long countByCondition(BiFunction<CriteriaBuilder, Root<T>, Predicate> condition) {
        return dao.countByCondition(condition);
    }

}
