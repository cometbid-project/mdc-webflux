/**
 * 
 */
package com.ndportmann.mdc_webflux.service.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * {@link Person} object stored inside a Redis {@literal HASH}. <br />
 * <br />
 * Sample (key = persons:9b0ed8ee-14be-46ec-b5fa-79570aadb91d):
 *
 * <pre>
 * <code>
 * _class := example.springdata.redis.domain.Person
 * id := 9b0ed8ee-14be-46ec-b5fa-79570aadb91d
 * firstname := eddard
 * lastname := stark
 * gender := MALE
 * address.city := winterfell
 * address.country := the north
 * children.[0] := persons:41436096-aabe-42fa-bd5a-9a517fbf0260
 * children.[1] := persons:1973d8e7-fbd4-4f93-abab-a2e3a00b3f53
 * children.[2] := persons:440b24c6-ede2-495a-b765-2d8b8d6e3995
 * children.[3] := persons:85f0c1d1-cef6-40a4-b969-758ebb68dd7b
 * children.[4] := persons:73cb36e8-add9-4ec0-b5dd-d820e04f44f0
 * children.[5] := persons:9c2461aa-2ef2-469f-83a2-bd216df8357f
 * </code>
 * </pre>
 *
 * @author Gbenga
 */
@Data
@RedisHash
@XmlRootElement
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Person extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8556262195844966313L;
	
	/**
	 * Using {@link Indexed} marks the property as for indexing which uses Redis
	 * {@literal SET} to keep track of {@literal ids} for objects with matching
	 * values. <br />
	 *
	 * <pre>
	 * <code>
	 * {@link RedisHash#value()} + ":" + {@link Field#getName()} +":" + {@link Field#get(Object)}
	 * //eg. persons:firstname:eddard
	 * </code>
	 * </pre>
	 */
	private @Indexed String firstname;
	private @Indexed String lastname;
	private Gender gender;

	/**
	 * Since {@link Indexed} is used on {@link Address#getCity()}, index structures
	 * for {@code persons:address:city} are maintained.
	 */
	@ToString.Exclude
	private Address address;

	/**
	 * Using {@link Reference} allows to link to existing objects via their
	 * {@literal key}. The values stored in the objects {@literal HASH} looks like:
	 *
	 * <pre>
	 * <code>
	 * children.[0] := persons:41436096-aabe-42fa-bd5a-9a517fbf0260
	 * children.[1] := persons:1973d8e7-fbd4-4f93-abab-a2e3a00b3f53
	 * children.[2] := persons:440b24c6-ede2-495a-b765-2d8b8d6e3995
	 * </code>
	 * </pre>
	 */
	@ToString.Exclude
	private @Reference List<Person> children;

	/**
	 * 
	 */
	private Person() {
		this(null, null, null);
	}
	
	/**
	 * @param firstname
	 * @param lastname
	 */
	public Person(String firstname, String lastname) {
		this(firstname, lastname, null);
	}

	/**
	 * @param firstname
	 * @param lastname
	 */
	public Person(String firstname, String lastname, Gender gender) {
		this(null, firstname, lastname, gender);
	}

	/**
	 * @param id
	 * @param firstname
	 * @param lastname
	 */
	public Person(String id, String firstname, String lastname, Gender gender) {
		this(id, firstname, lastname, gender, null);
	}

	/**
	 * @param id
	 * @param firstname
	 * @param lastname
	 * @param gender
	 * @param address
	 * @param children
	 */
	public Person(String id, String firstname, String lastname, Gender gender, Address address) {
		super(id);
		this.firstname = firstname;
		this.lastname = lastname;
		this.gender = gender;
		this.address = address;
		this.children = null;
	}

	/**
	 * @param id
	 * @param firstname
	 * @param lastname
	 * @param gender
	 * @param address
	 * @param children
	 * @param version
	 */
	public Person(String id, String firstname, String lastname, Gender gender, Address address, List<Person> children) {
		super(id);
		this.firstname = firstname;
		this.lastname = lastname;
		this.gender = gender;
		this.address = address;
		this.children = children;
		//this.version = version;
	}

}
