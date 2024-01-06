/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.service.common;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.collect.Lists;
import com.ndportmann.mdc_webflux.orm.jpa.FooId;

/**
 * @author Gbenga
 *
 */
@Transactional
public abstract class AbstractService<T extends Serializable, U> implements IOperations<T, U> {

	public FooId createId(String id) {
		return new FooId(id);
	}

	public FooId createId(Long id) {
		return new FooId(String.valueOf(id));
	}

	// read - one
	@Override
	@Transactional(readOnly = true)
	public T findById(final U id) {
		return getDao().findById(id).orElse(null);
	}

	// read - all
	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = true) public List<T> findAll() { return
	 * Lists.newArrayList(getDao().findAll()); }
	 */

	// write
	@Override
	public T create(final T entity) {
		return getDao().save(entity);
	}

	@Override
	public T update(final T entity) {
		return getDao().save(entity);
	}

	@Override
	public void delete(final T entity) {
		getDao().delete(entity);
	}

	@Override
	public void deleteById(final U entityId) {
		getDao().deleteById(entityId);
	}

	protected abstract ListCrudRepository<T, U> getDao();

}
