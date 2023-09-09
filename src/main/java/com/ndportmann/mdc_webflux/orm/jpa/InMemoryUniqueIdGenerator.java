/**
 * 
 */
package com.ndportmann.mdc_webflux.orm.jpa;

import java.util.UUID;

/**
 * 
 * @author Gbenga
 *
 */
public class InMemoryUniqueIdGenerator implements UniqueIdGenerator<UUID> {
	
    @Override
    public UUID getNextUniqueId() {
        return UUID.randomUUID();
    }
}
