/**
 * 
 */
package com.ndportmann.mdc_webflux.exceptions;

import lombok.Getter;

/**
 * @author Gbenga
 *
 */
@Getter
public class ServiceException extends RuntimeException {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -5915871408771766371L;
	
	private int statusCode; 

	public ServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
