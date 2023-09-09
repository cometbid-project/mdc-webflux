/**
 * 
 */
package com.ndportmann.mdc_webflux.orm.jpa;

/**
 * Interface for entity objects.
 *
 * @author Gbenga
 * 
 * @param <T> the type of {@link EntityId} that will be used in this entity
 */
public interface Entity<T extends EntityId> {

	T getId();
}