/**
 * 
 */
package com.ndportmann.mdc_webflux.exceptions;

import am.ik.yavi.core.ConstraintViolation;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Gbenga
 *
 */

@AllArgsConstructor
public class ValidationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8317922488936753385L;

	@Getter
	final List<ConstraintViolation> errors;

}
