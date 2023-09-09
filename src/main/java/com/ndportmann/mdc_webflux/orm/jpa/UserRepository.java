/**
 * 
 */
package com.ndportmann.mdc_webflux.orm.jpa;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Gbenga
 *
 */
public interface UserRepository extends CrudRepository<User, UUID>, UserRepositoryCustom {
	
	Optional<User> findByEmailIgnoreCase(String email);
}
