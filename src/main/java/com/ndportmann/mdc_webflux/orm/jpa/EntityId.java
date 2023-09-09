/**
 * 
 */
package com.ndportmann.mdc_webflux.orm.jpa;

import java.io.Serializable;

/**
 * Interface for primary keys of entities.
 *
 * @author Gbenga
 * @param <T> the underlying type of the entity id
 */
public interface EntityId<T> extends Serializable {

	T getId();

	String asString();
}