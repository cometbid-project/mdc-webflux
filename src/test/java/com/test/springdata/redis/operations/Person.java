/**
 * 
 */
package com.test.springdata.redis.operations;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * Example value object.
 *
 * @author Mark Paluch
 */
@Data
//@RequiredArgsConstructor
public class Person {

	final String firstname;
	final String lastname;

	/**
	 * @param firstname
	 * @param lastname
	 */
	public Person(String firstname, String lastname) {
		super();
		this.firstname = firstname;
		this.lastname = lastname;
	}
	
	/**
	 * @param firstname
	 * @param lastname
	 */
	public Person(String id, String firstname, String lastname) {
		super();
		this.firstname = firstname;
		this.lastname = lastname;
	}

	/**
	 * 
	 */
	public Person() {
		this(null, null);
	}

}
