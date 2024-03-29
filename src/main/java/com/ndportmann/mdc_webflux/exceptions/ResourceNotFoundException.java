/**
 * 
 */
package com.ndportmann.mdc_webflux.exceptions;

import org.springframework.http.HttpStatus;

import com.ndportmann.mdc_webflux.helpers.ResourceBundleAccessor;
import lombok.Getter;

/**
 * @author Gbenga
 *
 */
public class ResourceNotFoundException extends ApplicationDefinedRuntimeException implements ErrorCode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6618023139439658341L;

	private int statusCode;

	private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

	/**
	 *
	 */
	public ResourceNotFoundException() {
		this(new Object[] {});
	}

	/**
	 *
	 * @param args
	 */
	public ResourceNotFoundException(Object[] args) {
		this("exception.notfound", args);
	}

	/**
	 *
	 * @param messagekey
	 * @param args
	 */
	public ResourceNotFoundException(String messagekey, Object[] args) {
		this(messagekey, args, null);
	}

	/**
	 *
	 * @param messagekey
	 * @param args
	 * @param ex
	 */
	public ResourceNotFoundException(String messagekey, Object[] args, Throwable ex) {
		super(STATUS, ResourceBundleAccessor.accessMessageInBundle(messagekey, args), ex);
	}

	/**
	 * 
	 */
	@Override
	public String getErrorCode() {
		return ErrorCode.GENERIC_NOT_FOUND_ERR_CODE;
	}

}
