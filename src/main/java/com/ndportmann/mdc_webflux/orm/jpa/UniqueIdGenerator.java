/**
 * 
 */
package com.ndportmann.mdc_webflux.orm.jpa;

/**
 * 
 * @author Gbenga
 *
 * @param <T>
 */
public interface UniqueIdGenerator<T> {

	T getNextUniqueId();
}
