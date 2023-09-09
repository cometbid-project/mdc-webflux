/**
 * 
 */
package com.ndportmann.mdc_webflux.exceptions;

/**
 * @author Gbenga
 *
 */
public class UnkownIdentifierException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 9088412207219775921L;


	public UnkownIdentifierException() {
        super();
    }


    public UnkownIdentifierException(String message) {
        super(message);
    }


    public UnkownIdentifierException(String message, Throwable cause) {
        super(message, cause);
    }
}
