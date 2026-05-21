package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.domain.Entity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.slf4j.event.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

@Repository
@Transactional(rollbackFor = Exception.class)
public interface EntityDao<T extends Entity> {

    @Logging(Level.INFO)
    @Transactional(readOnly = true)
    default List<T> findByCondition(BiFunction<CriteriaBuilder, Root<T>, Predicate> condition) {
        return findAll((root, query, cb) -> condition.apply(cb, root));
    }

    @Logging(Level.INFO)
    @Transactional(readOnly = true)
    List<T> findAll(org.springframework.data.jpa.domain.Specification<T> spec);
//
//    @Logging(Level.INFO)
//    @Transactional(readOnly = true)
//    Optional<T> findById(Long id);

}
