/**
 * 
 */
package com.ndportmann.mdc_webflux.orm.jpa;

import java.util.UUID;

/**
 * @author Gbenga
 *
 */
public class UserRepositoryImpl implements UserRepositoryCustom {
	
	private final UniqueIdGenerator<UUID> generator;

	public UserRepositoryImpl(UniqueIdGenerator<UUID> generator) {
		this.generator = generator;
	}

	@Override
	public UserId nextId() {
		return new UserId(generator.getNextUniqueId());
	}
}
