/**
 * 
 */
package com.ndportmann.mdc_webflux.exceptions;

/**
 * @author Gbenga
 *
 */
public class InvalidTokenException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7140450468958904997L;


	public InvalidTokenException() {
        super();
    }


    public InvalidTokenException(String message) {
        super(message);
    }


    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
