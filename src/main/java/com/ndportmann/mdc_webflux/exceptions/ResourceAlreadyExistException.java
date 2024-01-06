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
public class ResourceAlreadyExistException extends ApplicationDefinedRuntimeException implements ErrorCode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7269022439669139584L;

	private int statusCode;

	private static final HttpStatus STATUS = HttpStatus.CONFLICT;

	/**
	 *
	 */
	public ResourceAlreadyExistException() {
		this(new Object[] {});
	}

	/**
	 *
	 * @param args
	 */
	public ResourceAlreadyExistException(Object[] args) {
		this("exception.alreadyExist", args);
	}

	/**
	 *
	 * @param messagekey
	 * @param args
	 */
	public ResourceAlreadyExistException(String messagekey, Object[] args) {
		this(messagekey, args, null);
	}

	/**
	 *
	 * @param messagekey
	 * @param args
	 * @param ex
	 */
	public ResourceAlreadyExistException(String messagekey, Object[] args, Throwable ex) {
		super(STATUS, ResourceBundleAccessor.accessMessageInBundle(messagekey, args), ex);
	}

	/**
	 * 
	 */
	@Override
	public String getErrorCode() {
		return ErrorCode.USER_EXIST_ERR_CODE;
	}

}
