/**
 * 
 */
package com.ndportmann.mdc_webflux.orm.jpa;

//import javax.persistence.EmbeddedId;
//import javax.persistence.MappedSuperclass;
import java.util.Objects;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.MoreObjects.toStringHelper;
import java.io.Serializable;
import java.util.Objects;

import com.ndportmann.mdc_webflux.util.ArtifactForFramework;

/**
 * Abstract super class for entities. We are assuming that early primary key
 * generation will be used.
 * 
 * @author Gbenga
 *
 * @param <T> the type of {@link EntityId} that will be used for this entity
 */
//@MappedSuperclass
public abstract class AbstractEntity<T extends EntityId> implements Entity<T>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6425982031170127365L;
	
	//@EmbeddedId
	private T id;

	@ArtifactForFramework
	protected AbstractEntity() {
	}

	public AbstractEntity(T id) {
		this.id = checkNotNull(id);
	}

	@Override
	public T getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (this == obj) {
			result = true;
		} else if (obj instanceof AbstractEntity) {
			AbstractEntity other = (AbstractEntity) obj;
			result = Objects.equals(id, other.id);
		}
		return result;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("id", id).toString();
	}
}
