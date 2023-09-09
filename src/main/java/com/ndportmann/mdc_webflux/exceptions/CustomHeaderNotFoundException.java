/**
 * 
 */
package com.ndportmann.mdc_webflux.exceptions;

/**
 * @author Gbenga
 *
 */
public class CustomHeaderNotFoundException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public CustomHeaderNotFoundException(String exception) {
		super(exception);
	}
}
