/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.services;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.collect.Lists;
import com.ndportmann.mdc_webflux.hateoas.dao.FooRepositoryImpl;
import com.ndportmann.mdc_webflux.hateoas.dao.IFooDao;
import com.ndportmann.mdc_webflux.hateoas.service.common.AbstractService;
import com.ndportmann.mdc_webflux.orm.jpa.FooId;
import com.ndportmann.mdc_webflux.orm.jpa.UniqueIdGenerator;
import com.ndportmann.mdc_webflux.repository.UserRepositoryImpl;
import com.ndportmann.mdc_webflux.service.model.Foo;

/**
 * @author Gbenga
 *
 */
@Service
@Transactional
public class FooServiceImpl extends AbstractService<Foo, FooId> implements IFooService {

	// @Autowired
	private final IFooDao dao;

	/**
	 * @param userRepository
	 */
	public FooServiceImpl(IFooDao dao) {
		super();
		this.dao = dao;
	}

	// API
	@Override
	public MongoRepository<Foo, FooId> getDao() {
		return dao;
	}

	// read - all
	@Override
	@Transactional(readOnly = true)
	public Page<Foo> findAll(int page, int size, Sort sort) {
		return getDao().findAll(PageRequest.of(page, size).withSort(sort));
	}

	@Override
	public Page<Foo> findPaginated(final int page, final int size) {
		return getDao().findAll(PageRequest.of(page, size));
	}

	@Override
	public Page<Foo> findPaginated(Pageable pageable) {
		return getDao().findAll(pageable);
	}

	// overridden to be secured

	@Override
	@Transactional(readOnly = true)
	public List<Foo> findAll(Sort sort) {
		return getDao().findAll(sort);
	}

}