/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.dao;

import java.util.UUID;
import com.ndportmann.mdc_webflux.orm.jpa.FooId;
import com.ndportmann.mdc_webflux.orm.jpa.UniqueIdGenerator;

/**
 * 
 */
public class FooRepositoryImpl implements FooRepositoryCustom {

	private final UniqueIdGenerator<UUID> generator;

	public FooRepositoryImpl(UniqueIdGenerator<UUID> generator) {
		this.generator = generator;
	}

	@Override
	public FooId nextId() {
		return new FooId(generator.getNextUniqueId().toString());
	}

}
