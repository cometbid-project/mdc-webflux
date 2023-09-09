/**
 * 
 */
package com.ndportmann.mdc_webflux.orm.jpa;

//import javax.persistence.MappedSuperclass;
import static com.google.common.base.MoreObjects.toStringHelper;
import java.io.Serializable;
import java.util.Objects;

import com.ndportmann.mdc_webflux.util.ArtifactForFramework;

/**
 * @author Gbenga
 *
 */
//@MappedSuperclass
public abstract class AbstractEntityId<T extends Serializable> implements Serializable, EntityId<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5962381739484024405L;
	
	private T id;

	@ArtifactForFramework
	protected AbstractEntityId() {
	}

	protected AbstractEntityId(T id) {
		this.id = Objects.requireNonNull(id);
	}

	@Override
	public T getId() {
		return id;
	}

	@Override
	public String asString() {
		return id.toString();
	}

	@Override
	public boolean equals(Object o) {
		boolean result = false;
		if (this == o) {
			result = true;
		} else if (o instanceof AbstractEntityId) {
			AbstractEntityId other = (AbstractEntityId) o;
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
