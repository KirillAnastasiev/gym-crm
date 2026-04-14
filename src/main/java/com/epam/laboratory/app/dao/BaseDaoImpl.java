package com.epam.laboratory.app.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public abstract class BaseDaoImpl<T, K> implements BaseDao<T, K> {

    protected final Map<String, Object> storage;

}
