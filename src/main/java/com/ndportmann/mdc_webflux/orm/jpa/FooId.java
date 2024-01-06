/**
 * 
 */
package com.ndportmann.mdc_webflux.orm.jpa;

/**
 * 
 */
public class FooId extends AbstractEntityId<String> {

	private static final long serialVersionUID = 242642184759063080L;

	protected FooId() {
	}

	public FooId(String id) {
		super(id);
	}
}
