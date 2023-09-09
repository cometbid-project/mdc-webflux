/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ndportmann.mdc_webflux.hateoas.service.common.IOperations;
import com.ndportmann.mdc_webflux.service.model.Foo;

/**
 * @author Gbenga
 *
 */
public interface IFooService extends IOperations<Foo> {
    
    Page<Foo> findPaginated(Pageable pageable);

}
