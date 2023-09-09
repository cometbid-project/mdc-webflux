/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.collect.Lists;
import com.ndportmann.mdc_webflux.hateoas.dao.IFooDao;
import com.ndportmann.mdc_webflux.hateoas.service.common.AbstractService;
import com.ndportmann.mdc_webflux.service.model.Foo;

/**
 * @author Gbenga
 *
 */
@Service
@Transactional
public class FooServiceImpl extends AbstractService<Foo> implements IFooService {

    @Autowired
    private IFooDao dao;

    public FooServiceImpl() {
        super();
    }

    // API

    @Override
    protected PagingAndSortingRepository<Foo, Long> getDao() {
        return dao;
    }

    // custom methods

    @Override
    public Page<Foo> findPaginated(Pageable pageable) {
        return dao.findAll(pageable);
    }
    
    // overridden to be secured

    @Override
    @Transactional(readOnly = true)
    public List<Foo> findAll() {
        return Lists.newArrayList(getDao().findAll());
    }

}