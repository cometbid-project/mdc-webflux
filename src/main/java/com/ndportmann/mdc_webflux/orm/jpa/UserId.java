/**
 * 
 */
package com.ndportmann.mdc_webflux.orm.jpa;

import java.util.UUID;

/**
 * @author Gbenga
 *
 */
public class UserId extends AbstractEntityId<UUID> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2986398400602668882L;

	protected UserId() {
	}

	public UserId(UUID id) {
		super(id);
	}
}
