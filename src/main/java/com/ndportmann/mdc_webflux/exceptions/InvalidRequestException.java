/**
 * 
 */
package com.ndportmann.mdc_webflux.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * @author Gbenga
 *
 */
public class InvalidRequestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6073988945101399813L;

	@Getter
	private int statusCode;

	public InvalidRequestException() {
		this(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase());
	}

	public InvalidRequestException(String message) {
		this(HttpStatus.NOT_FOUND, message);
	}

	public InvalidRequestException(String message, Throwable cause) {
		this(HttpStatus.NOT_FOUND, message, cause);
	}

	public InvalidRequestException(HttpStatus status, String message) {
		this(status, message, null);
	}

	public InvalidRequestException(HttpStatus status, Throwable cause) {
		this(status, status.getReasonPhrase(), cause);
	}

	public InvalidRequestException(HttpStatus status, String message, Throwable cause) {
		// TODO Auto-generated constructor stub
		super(message, cause);
		this.statusCode = status.value();
	}

}
