/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.service.common;

import java.io.Serializable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

/**
 * @author Gbenga
 *
 */
public interface IOperations<T extends Serializable, U> {

    // read - one

    T findById(final U id);

    // read - all
    Page<T> findAll(int page, int size, Sort sort);

    Page<T> findPaginated(int page, int size);

    // write
    T create(final T entity);

    T update(final T entity);

    void delete(final T entity);

    void deleteById(final U entityId);
}