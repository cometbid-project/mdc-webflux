/**
 * 
 */
package com.ndportmann.mdc_webflux.service.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Gbenga
 *
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Entity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8593647898506326880L;

	/**
	 * The {@literal id} and {@link RedisHash#toString()} build up the
	 * {@literal key} for the Redis {@literal HASH}. <br />
	 *
	 * <pre>
	 * <code>
	 * {@link RedisHash#value()} + ":" + {@link Person#id}
	 * //eg. persons:9b0ed8ee-14be-46ec-b5fa-79570aadb91d
	 * </code>
	 * </pre>
	 *
	 * <strong>Note:</strong> empty {@literal id} fields are automatically assigned
	 * during save operation.
	 */
	@NotNull
	@EqualsAndHashCode.Include
	protected @Id String id;

	@Version
	@Transient
	protected int version; // Used for data optimistic lock

	/**
	 * @param id
	 */
	protected Entity(@NotNull String id) {
		super();
		this.id = id;
	}

}
