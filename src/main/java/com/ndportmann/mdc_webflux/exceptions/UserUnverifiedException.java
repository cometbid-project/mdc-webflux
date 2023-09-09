/**
 * 
 */
package com.ndportmann.mdc_webflux.exceptions;

/**
 * @author Gbenga
 *
 */
public class UserUnverifiedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6487631223668994187L;

	public UserUnverifiedException() {
		super();
	}

	public UserUnverifiedException(String message) {
		super(message);
	}

	public UserUnverifiedException(String message, Throwable cause) {
		super(message, cause);
	}
}
