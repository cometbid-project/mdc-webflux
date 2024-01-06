/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.ndportmann.mdc_webflux.hateoas.service.common.IOperations;
import com.ndportmann.mdc_webflux.orm.jpa.FooId;
import com.ndportmann.mdc_webflux.service.model.Foo;

/**
 * @author Gbenga
 *
 */
public interface IFooService extends IOperations<Foo, FooId> {

	FooId createId(String id);

	FooId createId(Long id);

	Page<Foo> findPaginated(Pageable pageable);

	List<Foo> findAll(Sort sort);

}
